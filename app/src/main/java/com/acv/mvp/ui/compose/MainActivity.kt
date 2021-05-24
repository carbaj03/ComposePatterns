package com.acv.mvp.ui.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.acv.mvp.presentation.*
import com.acv.mvp.presentation.middleware.LoggerMiddleware
import com.acv.mvp.redux.ThunkMiddleware
import com.acv.mvp.redux.combineEnhancer
import com.acv.mvp.redux.combineReducers
import com.acv.mvp.ui.compose.theme.MvpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

val Store: StoreProvider<TodosState> =
    provide(
        store = createStore(
            combineReducers(NavigationReducer, TodoReducer, TodoDetailReducer),
            TodosState.initialState(),
            combineEnhancer(
                applyMiddleware(
                    ThunkMiddleware(),
                    LoggerMiddleware(
                        coroutineContext = Dispatchers.IO + SupervisorJob(),
                    ),
                ),
                applyA()
            )
        )
    )

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Store {
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
    when (val navigator = Store.useSelector { navigation }.value) {
        is TodoDetail -> TodoDetailScreen(navigator.id)
        is TodoList -> TodoListScreen()
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