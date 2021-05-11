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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.acv.mvp.presentation.*
import com.acv.mvp.ui.compose.theme.MvpTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

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

@OptIn(InternalComposeApi::class)
@Composable
fun <A> useSelector(f: (TodosState) -> A): State<A> {
    val store: TodosStore = viewModel()

    val selector: Flow<A> = store.state.map { f(it) }

    val result: MutableState<A> = remember {
        mutableStateOf(f(store.state.value))
    }

    val applyContext = currentComposer.applyCoroutineContext
    remember(selector) {
        object : RememberObserver {
            private val scope = CoroutineScope(applyContext)
            private var job: Job? = null

            override fun onRemembered() {
                job?.cancel("Old job was still running!")
                job = scope.launch {
                    selector.collect { result.value = it }
                }
            }

            override fun onForgotten() {
                job?.cancel()
                job = null
            }

            override fun onAbandoned() {
                job?.cancel()
                job = null
            }
        }
    }
    return result
}
//    val s: Flow<A> = store.state.map { f(it) }
//    return selector.collectAsState(f(store.state.value))

@Composable
fun useDispatch(): State<(Action) -> Unit> {
    val store = viewModel<TodosStore>()
    return remember { mutableStateOf({ action: Action -> store.action(action) }) }
}

@Composable
fun App() {
    val todos by useSelector { it.todos }

    Column {
        Header()

        Header2()

        TodoList(todos = todos)

        Footer(
            count = 0,
            onClearCompleted = {},
            markAllCompleted = {},
            onSelected = { },
        )
    }
}

@Composable
fun Header2() {
    Log.e("Compose", "Header2")
    val text by useSelector { it.input2 }
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
                dispatcher(InputChange2(""))
            }
        ),
        onValueChange = {
            dispatcher(InputChange2(it))
        }
    )
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
fun TodoList(todos: List<Todo>) {
    Log.e("Compose", "TodoList")
    LazyColumn {
        items(todos) {
            Text(
                text = it.text,
                color = if (it.completed) Color.Black else Color.LightGray
            )
        }
    }
}

@Composable
fun Footer(
    count: Int,
    markAllCompleted: () -> Unit,
    onClearCompleted: () -> Unit,
    onSelected: (Filter) -> Unit,
) {
    Log.e("Compose", "Footer")
    Column {
        Button(onClick = { markAllCompleted() }) {
            Text("Mark All Completed")
        }
        Button(onClick = { onClearCompleted() }) {
            Text(text = "Clear Completed")
        }
        RemainingTodos(count)
        StatusFilter(onSelected = onSelected)
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
fun StatusFilter(onSelected: (Filter) -> Unit) {
    Log.e("Compose", "StatusFilter")
    var currentFilter by remember {
        mutableStateOf(Filter.All)
    }

    val color = { filter: Filter ->
        if (filter == currentFilter) Color.LightGray else Color.Transparent
    }

    Text(text = "Remaining Todos")
    Row {
        Text(
            text = "All",
            modifier = Modifier
                .clickable {
                    currentFilter = Filter.All
                    onSelected(currentFilter)
                }
                .background(color = color(Filter.All))
        )
        Text(
            text = "Active",
            modifier = Modifier
                .clickable {
                    currentFilter = Filter.Active
                    onSelected(currentFilter)
                }
                .background(color = color(Filter.Active))
        )
        Text(
            text = "Completed",
            modifier = Modifier
                .clickable {
                    currentFilter = Filter.Completed
                    onSelected(currentFilter)
                }
                .background(color = color(Filter.Completed))
        )
    }
}

data class Todo(
    val id: Int,
    val text: String,
    val completed: Boolean,
)

enum class Filter {
    All, Active, Completed
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