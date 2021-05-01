package com.acv.mvp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.acv.mvp.ui.theme.MvpTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val todos = listOf(
            "Buy bread",
            "Create a post",
        )
        setContent {
            MvpTheme {
                Surface(color = MaterialTheme.colors.background) {
                    var state by remember { mutableStateOf(todos) }
                    LazyColumn {
                        items(state) { todoItem ->
                            Text(text = todoItem)
                        }
                        item {
                            Row {
                                var text by remember { mutableStateOf("") }
                                TextField(
                                    value = text,
                                    onValueChange = { text = it }
                                )
                                Button(onClick = { state = state.plus(text) }) {
                                    Text(text = "Add")
                                }
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
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MvpTheme {
        Greeting("Android")
    }
}