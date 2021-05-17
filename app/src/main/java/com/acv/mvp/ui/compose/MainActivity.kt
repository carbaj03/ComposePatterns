package com.acv.mvp.ui.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acv.mvp.R
import com.acv.mvp.data.Repository
import com.acv.mvp.presentation.*
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.Reducer
import com.acv.mvp.redux.SideEffect
import com.acv.mvp.redux.combineReducers
import com.acv.mvp.ui.compose.theme.MvpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreFactory<A : Action, S : StoreState>(
    private val sideEffects: List<SideEffect<A, S>>,
    private val reducer: Reducer<S>,
    private val initial: S
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodosStore::class.java)) {
            return TodosStore(
                sideEffects = sideEffects,
                reducer = reducer,
                initialState = initial
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MainActivity : ComponentActivity() {

    @InternalComposeApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MvpTheme {
                Surface(color = MaterialTheme.colors.background) {
                    App()
                }
            }
        }
    }
}

val reducers: Reducer<TodosState> =
    combineReducers(TodoReducer, TodoDetailReducer)

val effects: List<SideEffect<TodoAction, TodosState>> =
    listOf(
        TodoSideEffect(
            repository = Repository,
            coroutineContext = Dispatchers.IO + SupervisorJob(),
        ),
        TodoDetailSideEffect(
            repository = Repository,
            coroutineContext = Dispatchers.IO + SupervisorJob(),
        ),
        LoggerSideEffect(),
    )

val storeFactory: StoreFactory<TodoAction, TodosState> =
    StoreFactory(
        sideEffects = effects,
        reducer = reducers,
        initial = TodosState.initalState()
    )

@Composable
fun <A> useSelector(f: (TodosState) -> A): State<A> {
    val store: TodosStore<TodoAction, TodosState> = viewModel(factory = storeFactory)
    val selector: Flow<A> = store.state.map { f(it) }
    return selector.collectAsState(f(store.state.value))
}

@Composable
fun <A : Action> useDispatch(): State<(A) -> Unit> {
    val store: TodosStore<A, TodosState> = viewModel(factory = storeFactory)
    return remember { mutableStateOf({ action: A -> store.dispatch(action) }) }
}

@Composable
fun App() {
    when (val navigator = useSelector { it.navigation }.value) {
        is TodoDetail -> TodoDetailScreen(navigator.id)
        is TodoList -> TodoListScreen()
    }
}

@Composable
fun TodoDetailScreen(id: Int) {
    Log.e("Compose", "TodoDetailScreen")
    val dispatcher by useDispatch<TodoDetailAction>()
    val todo by useSelector { it.detail }
    val error by useSelector { it.error }
    val loading by useSelector { it.loading }

    LaunchedEffect(id) { dispatcher(GetTodo(id)) }

    if (error) {
        Text(text = "ERROR")
        Button(onClick = { dispatcher(GetTodo(id)) }) {
            Text(text = "Retry")
        }
    } else if (loading)
        Text(text = "Loading")
    else
        Column {
            Text(text = todo?.text.toString())
            Text(text = todo?.completed.toString())
            Button(onClick = { dispatcher(ShowTodos) }) {
                Text(text = "Back")
            }
        }
}

@Composable
fun TodoListScreen() {
    Log.e("Compose", "TodoListScreen")
    val todos by useSelector { it.filterBy() }
    val itemsLeft by useSelector { it.itemsLeft() }
    val dispatcher by useDispatch<TodoListAction>()

    dispatcher(LoadTodos)

    Column {
        Header()

        TodoList(
            todos = todos,
            onItemSelected = { isCompleted, todo ->
                if (isCompleted) dispatcher(CompleteTodo(todo.id))
                else dispatcher(ActivateTodo(todo.id))
            }
        )

        Footer(count = itemsLeft)
    }
}

@Composable
fun Header() {
    Log.e("Compose", "Header1")
    val text by useSelector { it.input }
    val dispatcher by useDispatch<TodoListAction>()

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                dispatcher(AddTodo(text))
                dispatcher(InputChange(""))
            }
        ),
        onValueChange = {
            dispatcher(InputChange(it))
        }
    )
}

@Composable
fun TodoList(
    todos: List<Todo>,
    onItemSelected: (Boolean, Todo) -> Unit,
) {
    Log.e("Compose", "TodoList")
    val dispatcher by useDispatch<TodoListAction>()

    LazyColumn {
        items(todos) { todo ->
            Row {
                Text(
                    text = todo.text,
                    color = if (todo.completed) Color.Black else Color.LightGray
                )
                Checkbox(
                    checked = todo.completed,
                    onCheckedChange = { onItemSelected(it, todo) }
                )
                Image(
                    modifier = Modifier.clickable { dispatcher(ShowDetail(todo.id)) },
                    painter = painterResource(R.drawable.ic_eye),
                    contentDescription = "See Detail of ${todo.text}",
                    colorFilter = ColorFilter.tint(color = Color.Blue)
                )
            }
        }
    }
}

@Composable
fun Footer(
    count: Int,
) {
    Log.e("Compose", "Footer")
    val dispatcher by useDispatch<TodoListAction>()

    Column {
        Button(onClick = { dispatcher(CompleteAll) }) {
            Text("Mark All Completed")
        }
        Button(onClick = { dispatcher(ClearCompleted) }) {
            Text(text = "Clear Completed")
        }
        RemainingTodos(count)
        StatusFilter()
    }
}

@Composable
fun RemainingTodos(count: Int) {
    Log.e("Compose", "RemainingTodos")
    Column {
        Text(text = "Remaining Todos")
        Text(text = "$count items left")
    }
}

@Composable
fun StatusFilter() {
    Log.e("Compose", "StatusFilter")
    val currentFilter by useSelector { it.filter }
    val dispatcher by useDispatch<TodoListAction>()

    val color: (Filter) -> Color = { filter: Filter ->
        if (filter == currentFilter) Color.LightGray else Color.Transparent
    }

    val modifier: (Filter) -> Modifier = { filter: Filter ->
        Modifier
            .padding(4.dp)
            .clickable { dispatcher(FilterBy(filter)) }
            .background(color = color(filter))
    }

    Text(text = "Filter by Status")
    Row {
        Text(
            text = "All",
            modifier = modifier(Filter.All)
        )
        Text(
            text = "Active",
            modifier = modifier(Filter.Active)
        )
        Text(
            text = "Completed",
            modifier = modifier(Filter.Completed)
        )
    }
}

data class Todo(
    val id: Int,
    val text: String,
    val completed: Boolean,
)

sealed class Filter {
    object All : Filter()
    object Active : Filter()
    object Completed : Filter()
}

@Composable
fun Greeting(name: String) {
    var text by remember { mutableStateOf("") }
    TextField(
        value = text,
        onValueChange = { text = it }
    )
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MvpTheme {
        Greeting("Android")
    }
}