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
import com.acv.mvp.R
import com.acv.mvp.presentation.*
import com.acv.mvp.presentation.middleware.LoggerMiddleware
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.Store
import com.acv.mvp.redux.ThunkMiddleware
import com.acv.mvp.redux.combineReducers
import com.acv.mvp.ui.compose.theme.MvpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MainActivity : ComponentActivity() {
    private val store: Store<TodosState, Action> = createStore(
        combineReducers(TodoReducer, TodoDetailReducer),
        TodosState.initalState(),
        applyMiddleware(
            ThunkMiddleware(),
            LoggerMiddleware(
                coroutineContext = Dispatchers.IO + SupervisorJob(),
            ),
        )
    )

    @InternalComposeApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Store(store) {
                MvpTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        App()
                    }
                }
            }
        }
    }
}

@Composable
fun App() {
    when (val navigator = useSelector<TodosState, Navigation> { navigation }.value) {
        is TodoDetail -> TodoDetailScreen(navigator.id)
        is TodoList -> TodoListScreen()
    }
}

@Composable
fun TodoDetailScreen(id: Int) {
    Log.e("Compose", "TodoDetailScreen")
    val dispatcher by useDispatch()

    val todo by useSelector<TodosState, Todo?> { detail }
    val error by useSelector<TodosState, Boolean> { error }
    val loading by useSelector<TodosState, Boolean> { loading }

    LaunchedEffect(id) {
        dispatcher(TodoDetailThunks.GetTodo(id))
    }

    if (error) {
        Text(text = "ERROR")
        Button(onClick = { dispatcher(GetTodo(id)) }) {
            Text(text = "Retry")
        }
    } else if (loading) {
        Text(text = "Loading")
    } else {
        Column {
            Text(text = todo?.text.toString())
            Text(text = todo?.completed.toString())
            Button(onClick = { dispatcher(ShowTodos) }) {
                Text(text = "Back")
            }
        }
    }
}


@Composable
fun TodoListScreen() {
    Log.e("Compose", "TodoListScreen")
    val todos by useSelector<TodosState, List<Todo>> { filterBy() }
    val itemsLeft by useSelector<TodosState, Int> { itemsLeft() }
    val dispatcher by useDispatch()

    dispatcher(TodoThunks.LoadTodos())

    Column {
        Header()

        TodoList(
            todos = todos,
            onItemSelected = { isCompleted, todo ->
                if (isCompleted) dispatcher(TodoThunks.CompleteTodo(todo.id))
                else dispatcher(TodoThunks.ActivateTodo(todo.id))
            }
        )

        Footer(count = itemsLeft)
    }
}

@Composable
fun Header() {
    Log.e("Compose", "Header1")
    val text by useSelector<TodosState, String> { input }
    val dispatcher by useDispatch()

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                dispatcher(TodoThunks.AddTodo(text))
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
    val dispatcher by useDispatch()

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
    val dispatcher by useDispatch()

    Column {
        Button(onClick = { dispatcher(TodoThunks.CompleteAll()) }) {
            Text("Mark All Completed")
        }
        Button(onClick = { dispatcher(TodoThunks.ClearCompleted()) }) {
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
    val currentFilter by useSelector<TodosState, Filter> { filter }
    val dispatcher by useDispatch()

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