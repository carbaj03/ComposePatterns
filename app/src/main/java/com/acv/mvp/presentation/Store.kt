package com.acv.mvp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class Action
object LoadTodos : Action()
data class LoadTodosSuccess(
    val todos: List<Todo>
) : Action()

data class AddTodo(val text: String) : Action()

data class TodosState(
    val todos: List<Todo>,
) {
    companion object {
        fun empty() = TodosState(emptyList())
    }
}


abstract class Store<A> : ViewModel() {
    abstract val state: StateFlow<A>
    abstract fun action(action: Action)
}

class Repository() {
    private var todos = listOf(Todo(0, "Start", false))

    suspend fun getAll(): List<Todo> =
        todos

    suspend fun put(todo: Todo): List<Todo> {
        todos = todos.plus(todo)
        return todos
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TodosStore(
    private val repository: Repository
) : Store<TodosState>() {
    override val state: MutableStateFlow<TodosState> = MutableStateFlow(TodosState.empty())

    override fun action(action: Action) {
        state.value = action.reduce(state.value)
        action.sideEffects()
    }

    private fun Action.reduce(currentState: TodosState): TodosState =
        when (this) {
            is LoadTodos -> currentState
            is LoadTodosSuccess -> currentState.copy(todos = todos)
        }

    private fun Action.sideEffects() {
        when (this) {
            is LoadTodos -> loadForm()
        }
    }

    private fun loadForm() {
        viewModelScope.launch {
            action(LoadTodosSuccess(repository.getAll()))
        }
    }
}