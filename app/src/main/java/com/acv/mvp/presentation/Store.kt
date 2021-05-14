package com.acv.mvp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.acv.mvp.data.Repository
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

abstract class Store<A> : ViewModel() {
    abstract val state: StateFlow<A>
    abstract fun action(action: Action)
}

interface SideEffect {
    fun next(action: Action, store: Store<*>)
}

class TodoSideEffect(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : SideEffect, CoroutineScope {
    override fun next(action: Action, store: Store<*>) {
        launch {
            when (action) {
                is LoadTodos -> {
                    store.action(LoadTodosSuccess(repository.getAll()))
                }
            }
        }
    }
}

class LoggerSideEffect() : SideEffect {
    override fun next(action: Action, store: Store<*>) {
        Log.e("oldState", store.state.value.toString())
        Log.e("logger", action.toString())
    }
}

class TodosStore(
    private val sideEffects: List<SideEffect>
) : Store<TodosState>() {
    override val state: MutableStateFlow<TodosState> =
        MutableStateFlow(TodosState.empty())

    override fun action(action: Action) {
        sideEffects.forEach { it.next(action, this) }
        state.value = state.value.reduce(action)
    }

    private fun TodosState.reduce(action: Action): TodosState =
        when (action) {
            is LoadTodos -> this
            is LoadTodosSuccess -> copy(todos = todos)
            is AddTodo -> copy(
                todos = todos.plus(
                    Todo(
                        id = todos.size + 1,
                        text = action.text,
                        completed = false,
                    )
                )
            )
            is InputChange -> copy(input = action.text)
            is InputChange2 -> copy(input2 = action.text)
            is ClearCompleted -> copy(todos = todos.filterNot { it.completed })
            is CompleteAll -> copy(todos = todos.map { it.copy(completed = true) })
            is CompleteTodo -> copy(
                todos = todos.update(
                    condition = { id == action.selectedId },
                    transform = { copy(completed = true) }
                )
            )
            is ActivateTodo -> copy(
                todos = todos.update(
                    condition = { id == action.selectedId },
                    transform = { copy(completed = false) }
                )
            )
            is FilterBy -> copy(filter = action.filter)
        }
}