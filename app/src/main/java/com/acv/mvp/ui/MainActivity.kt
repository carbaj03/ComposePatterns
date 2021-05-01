package com.acv.mvp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
                    LazyColumn {
                        items(todos) { todoItem ->
                            Text(text = todoItem)
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