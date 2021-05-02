package com.acv.mvp.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.acv.mvp.domain.Task
import com.acv.mvp.domain.Tasks
import com.acv.mvp.ui.compose.theme.MvpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MvpTheme {
                Surface(color = MaterialTheme.colors.background) {
                    var tasks by remember {
                        mutableStateOf(
                            Tasks(
                                tasks = listOf(
                                    Task(id = 1, task = "Create Todo App"),
                                    Task(id = 2, task = "Create Post"),
                                )
                            )
                        )
                    }
                    Column {
                        Row {
                            var text by remember { mutableStateOf("") }
                            TextField(
                                value = text,
                                onValueChange = { text = it }
                            )
                            val onClick: () -> Unit = {
                                tasks = Tasks(
                                    tasks = tasks.tasks.plus(Task(tasks.tasks.size, text))
                                )
                                text = ""
                            }
                            Button(onClick = onClick) {
                                Text(text = "Add")
                            }
                        }

                        LazyColumn {
                            items(tasks.tasks) { task ->
                                Text(text = task.task)
                            }
                        }
                    }
                }
            }
        }
    }
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