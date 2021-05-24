package com.acv.mvp.ui.compose

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import com.acv.mvp.presentation.GetTodo

@Composable
fun TodoDetailScreen(id: Int) {
    Log.e("Compose", "TodoDetailScreen")
    val dispatcher by Store.useDispatch()

    val todo by Store.useSelector { getTodo() }
    val error by Store.useSelector { error }
    val loading by Store.useSelector { loading }

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
            Button(onClick = { dispatcher(com.acv.mvp.presentation.TodoList) }) {
                Text(text = "Back")
            }
        }
    }
}