package com.acv.mvp.ui.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acv.mvp.data.Repository
import com.acv.mvp.presentation.*
import com.acv.mvp.ui.compose.theme.MvpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class StoreFactory(private val sideEffects: List<SideEffect>) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodosStore::class.java)) {
            return TodosStore(sideEffects = sideEffects) as T
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

val storeFactory = StoreFactory(
    listOf(
        TodoSideEffect(
            repository = Repository(),
            coroutineContext = Dispatchers.IO + SupervisorJob(),
        ),
        LoggerSideEffect()
    )
)

@Composable
fun <A> useSelector(f: (TodosState) -> A): State<A> {
    val store: TodosStore = viewModel(factory = storeFactory)
    val selector: Flow<A> = store.state.map { f(it) }
    return selector.collectAsState(f(store.state.value))
}

@Composable
fun useDispatch(): State<(Action) -> Unit> {
    val store: TodosStore = viewModel(factory = storeFactory)
    return remember { mutableStateOf({ action: Action -> store.action(action) }) }
}

@Composable
fun App() {
    val todos by useSelector { it.filterBy() }
    val itemsLeft by useSelector { it.itemsLeft() }
    val dispatcher by useDispatch()

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
    val dispatcher by useDispatch()

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