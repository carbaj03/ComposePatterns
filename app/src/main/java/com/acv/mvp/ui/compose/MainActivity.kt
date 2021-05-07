package com.acv.mvp.ui.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.acv.mvp.presentation.*
import com.acv.mvp.ui.compose.theme.MvpTheme

class MainActivity : ComponentActivity() {
    private val store = Store()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store.action(LoadTasks)
        setContent {
            MvpTheme {
                Log.e("render", "MvpTheme")
                Surface(color = MaterialTheme.colors.background) {
                    Log.e("render", "Surface")
                    val s by store.state.collectAsState()
                    when (val state = s) {
                        Error -> Column {
                            Text(text = "Error")
                            Button(onClick = { store.action(LoadTasks) }) {
                                Text(text = "Reload")
                            }
                        }
                        Loading -> Text(text = "Loading")
                        is Success ->
                            Column {
                                Log.e("render", "Column")
                                Row {
                                    Log.e("render", "Row")
                                    TextField(
                                        value = state.input,
                                        onValueChange = { store.action(ChangeInput(it)) }
                                    )
                                    Button(onClick = { store.action(AddTask(state.input)) }) {
                                        Log.e("render", "Button")
                                        Text(text = "Add")
                                    }
                                    Row {
                                        Log.e("render", "Row2")
                                        Row {
                                            Log.e("render", "Row3")
                                        }
                                    }
                                }

                                LazyColumn {
                                    items(state.tasks.tasks) { task ->
                                        Text(text = task.task)
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