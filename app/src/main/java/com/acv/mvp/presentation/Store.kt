package com.acv.mvp.presentation

import androidx.lifecycle.ViewModel
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

abstract class Store<A> : ViewModel() {
    abstract val state: StateFlow<A>
    abstract fun action(action: Action)
}

@OptIn(ExperimentalCoroutinesApi::class)
class TodosStore(
//    private val repository: Repository
) : Store<TodosState>() {
    override val state: MutableStateFlow<TodosState> =
        MutableStateFlow(TodosState.empty())

    override fun action(action: Action) {
        state.value = action.reduce(state.value)
//        action.sideEffects()
    }

    private fun Action.reduce(currentState: TodosState): TodosState =
        when (this) {
            is LoadTodos -> currentState
            is LoadTodosSuccess -> currentState.copy(todos = todos)
            is AddTodo -> currentState.copy(
                todos = currentState.todos.plus(
                    Todo(
                        id = currentState.todos.size + 1,
                        text = text,
                        completed = false,
                    )
                )
            )
            is InputChange -> currentState.copy(input = text)
            is InputChange2 -> currentState.copy(input2 = text)
            is ClearCompleted -> currentState.copy(todos = currentState.todos.filterNot { it.completed })
            is CompleteAll -> currentState.copy(todos = currentState.todos.map { it.copy(completed = true) })
            is CompleteTodo -> {
                currentState.copy(
                    todos = currentState.todos.update(
                        condition = { id == selectedId },
                        transform = { copy(completed = true) }
                    )
                )

            }
            is ActivateTodo -> currentState.copy(
                todos = currentState.todos.update(
                    condition = { id == selectedId },
                    transform = { copy(completed = false) }
                )
            )
            is FilterBy -> currentState.copy(filter = filter)
        }

//    private fun Action.sideEffects() {
//        when (this) {
//            is LoadTodos -> loadForm()
//        }
//    }

//    private fun loadForm() {
//        viewModelScope.launch {
//            action(LoadTodosSuccess(repository.getAll()))
//        }
//    }
}