package com.acv.mvp.ui.compose

import android.os.Bundle
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
import com.acv.mvp.ui.compose.theme.MvpTheme


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

@Composable
fun App() {
    var todos by remember { mutableStateOf(emptyList<Todo>()) }

    Column {
        Header(
            onDone = {
                todos = todos.plus(
                    Todo(
                        id = todos.count(),
                        text = it,
                        completed = false,
                    )
                )
            }
        )

        TodoList(todos = todos)

        Footer(
            count = todos.count(),
            onClearCompleted = {
                todos = todos.filterNot { it.completed }
            },
            markAllCompleted = {
                todos = todos.map { it.copy(completed = true) }
            },
            onSelected = { filter ->
                when (filter) {
                    Filter.All -> todos
                    Filter.Active -> {
                        todos = todos.filterNot { it.completed }
                    }
                    Filter.Completed -> {
                        todos = todos.filter { it.completed }
                    }
                }
            }
        )
    }
}


@Composable
fun Footer(
    count: Int,
    markAllCompleted: () -> Unit,
    onClearCompleted: () -> Unit,
    onSelected: (Filter) -> Unit,
) {
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
    Column {
        Text(
            text = "Remaining Todos",
        )
        Text(
            text = "$count items left"
        )
    }
}

enum class Filter {
    All, Active, Completed
}

@Composable
fun StatusFilter(onSelected: (Filter) -> Unit) {
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

@Composable
fun TodoList(todos: List<Todo>) {
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
fun Header(onDone: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    TextField(
        modifier = Modifier.fillMaxWidth(),
        value = text,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onDone(text)
                text = ""
            }
        ),
        onValueChange = { text = it }
    )
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