package com.acv.mvp.ui.compose

import android.util.Log
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
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.acv.mvp.R
import com.acv.mvp.presentation.FilterBy
import com.acv.mvp.presentation.InputChange
import com.acv.mvp.presentation.middleware.NavigateTo
import com.acv.mvp.presentation.middleware.Screen

@Composable
fun TodoListScreen(navController: NavHostController) {
    Log.e("Compose", "TodoListScreen")
    val todos by Store.useSelector { filterBy() }
    val itemsLeft by Store.useSelector { itemsLeft() }
    val dispatcher by Store.useDispatch()

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
    val text by Store.useSelector { input }
    val dispatcher by Store.useDispatch()

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
    val dispatcher by Store.useDispatch()

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
                    modifier = Modifier.clickable {
                        dispatcher(NavigateTo(Screen.TodoDetail(todo.id)))
                    },
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
    val dispatcher by Store.useDispatch()

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
    val currentFilter by Store.useSelector { filter }
    val dispatcher by Store.useDispatch()

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