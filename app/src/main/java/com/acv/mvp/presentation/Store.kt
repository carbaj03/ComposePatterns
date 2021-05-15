package com.acv.mvp.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import com.acv.mvp.data.Repository
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.Reducer
import com.acv.mvp.redux.SideEffect
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


interface StoreState

abstract class Store<S : StoreState, A : Action> : ViewModel() {
    abstract val state: StateFlow<S>
    abstract fun dispatch(action: A)
}

val TodoDetailReducer: Reducer<TodosState> =
    Reducer<TodosState, TodoDetailAction> { action ->
        when (action) {
            is GetTodo -> this
        }
    }

val TodoReducer: Reducer<TodosState> =
    Reducer<TodosState, TodoListAction> { action ->
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
            is ShowDetail -> copy(navigation = TodoDetail(action.id))
        }
    }

class TodoSideEffect(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : SideEffect<TodoAction, TodosState>, CoroutineScope {
    override fun invoke(action: TodoAction, store: Store<TodosState, TodoAction>): TodoAction {
        launch {
            when (action) {
                is LoadTodos -> {
                    val todos = repository.getAll()
                    store.dispatch(LoadTodosSuccess(todos))
                }
            }
        }
        return action
    }
}

class LoggerSideEffect(
    override val coroutineContext: CoroutineContext,
) : SideEffect<TodoAction, TodosState>, CoroutineScope {
    override fun invoke(action: TodoAction, store: Store<TodosState, TodoAction>): TodoAction {
        launch {
            Log.e("oldState", store.state.value.toString())
            Log.e("logger", action.toString())
        }
        return action
    }
}

class TodosStore<A : Action, S : StoreState>(
    private val sideEffects: List<SideEffect<A, S>>,
    private val reducer: Reducer<S>,
    initialState: S
) : Store<S, A>() {
    override val state: MutableStateFlow<S> =
        MutableStateFlow(initialState)

    override fun dispatch(action: A) {
        sideEffects.forEach { it(action, this) }
        state.value = state.value.reduce(action)
    }

    private fun S.reduce(action: A): S =
        reducer(this, action)
}