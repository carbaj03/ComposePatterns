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

abstract class Store<S> : ViewModel() {
    abstract val state: StateFlow<S>
    abstract fun dispatch(action: Action)
}

fun interface Reducer<S, A : Action> {
    operator fun S.invoke(action: A): S
}

val TodoReducer = Reducer<TodosState, Action> { action ->
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
        is InputChange -> copy(
            input = action.text
        )
        is ClearCompleted -> copy(
            todos = todos.filterNot { it.completed }
        )
        is CompleteAll -> copy(
            todos = todos.map { it.copy(completed = true) }
        )
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
        is FilterBy -> copy(
            filter = action.filter
        )
    }
}

fun interface SideEffect<A : Action> {
    operator fun invoke(action: A, store: Store<*>)
}

class TodoSideEffect(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : SideEffect<Action>, CoroutineScope {
    override fun invoke(action: Action, store: Store<*>) {
        launch {
            when (action) {
                is LoadTodos -> {
                    val todos = repository.getAll()
                    store.dispatch(LoadTodosSuccess(todos))
                }
            }
        }
    }
}

class LoggerSideEffect() : SideEffect<Action> {
    override fun invoke(action: Action, store: Store<*>) {
        Log.e("oldState", store.state.value.toString())
        Log.e("logger", action.toString())
    }
}

class TodosStore(
    private val sideEffects: List<SideEffect<Action>>,
    private val reducer: Reducer<TodosState, Action>,
) : Store<TodosState>() {
    override val state: MutableStateFlow<TodosState> =
        MutableStateFlow(TodosState.empty())

    override fun dispatch(action: Action) {
        sideEffects.forEach { it(action, this) }
        state.value = state.value.reduce(action)
    }

    private fun TodosState.reduce(action: Action): TodosState =
        reducer.run { invoke(action) }
}