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
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.acv.mvp.presentation.*
import com.acv.mvp.presentation.middleware.LoggerMiddleware
import com.acv.mvp.presentation.middleware.NavigationMiddleware
import com.acv.mvp.presentation.middleware.Navigator
import com.acv.mvp.presentation.middleware.Screen
import com.acv.mvp.redux.ThunkMiddleware
import com.acv.mvp.redux.combineEnhancer
import com.acv.mvp.redux.combineReducers
import com.acv.mvp.ui.compose.theme.MvpTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


class ComposeNavigator(
    private var navController: NavHostController? = null
) : Navigator {
    override fun goTo(screen: Screen) {
        when (screen) {
            is Screen.TodoDetail ->
                navController?.navigate("todoDetail/${screen.id}")
            is Screen.TodoList ->
                navController?.navigate("todoList")
        }
    }

    override fun goBack() {
        navController?.popBackStack()
    }

    @Composable
    fun create(navHostController: NavHostController) {
        navController = navHostController

        NavHost(navController = navHostController, startDestination = "todoList") {
            composable("todoDetail/{todoId}") {
                it.arguments?.getString("todoId")?.toIntOrNull()?.let { id ->
                    TodoDetailScreen(id)
                }
            }
            composable("todoList") {
                TodoListScreen(navHostController)
            }
        }
    }

    fun unSubscribe() {
        navController = null
    }
}

val navigator = ComposeNavigator()

val Store: StoreProvider<TodosState> =
    provide(
        store = createStore(
            combineReducers(TodoReducer, TodoDetailReducer),
            TodosState.initialState(),
            combineEnhancer(
                applyMiddleware(
                    NavigationMiddleware(navigator),
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
                        App(navigator)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        navigator.unSubscribe()
    }
}

@Composable
fun App(navigator: ComposeNavigator) {
    navigator.create(navHostController = rememberNavController())
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