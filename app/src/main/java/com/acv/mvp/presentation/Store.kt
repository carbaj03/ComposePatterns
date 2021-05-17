package com.acv.mvp.presentation

import androidx.lifecycle.ViewModel
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.Dispatcher
import com.acv.mvp.redux.Middleware
import com.acv.mvp.redux.Reducer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


interface StoreState

abstract class Store<S : StoreState, A : Action> : ViewModel() {
    abstract val state: StateFlow<S>
    abstract fun dispatch(action: A)
}

interface StoreCreator<A : Action, S : StoreState> {
    val reducer: Reducer<S>
    val initialState: S
    val middlewares: List<Middleware<A, S>>
}

val TodoDetailReducer: Reducer<TodosState> =
    Reducer<TodosState, TodoDetailAction> { action ->
        when (action) {
            is GetTodo -> copy(loading = true)
            is GetTodoSuccess -> copy(detail = action.todo, loading = false)
            is GetTodoError -> copy(error = true, loading = false)
            is ShowTodos -> copy(navigation = TodoList, error = false, loading = false)
        }
    }

val TodoReducer: Reducer<TodosState> =
    Reducer<TodosState, TodoListAction> { action ->
        when (action) {
            is LoadTodos -> this
            is LoadTodosSuccess -> copy(todos = action.todos)
            is LoadTodosError -> copy(error = true)
            is AddTodo -> this
            is AddTodoError -> copy(error = true)
            is AddTodoSuccess -> copy(todos = action.todos)
            is InputChange -> copy(input = action.text)
            is ClearCompleted -> this
            is ClearCompletedSuccess -> copy(todos = action.todos)
            is ClearCompletedError -> copy(error = true)
            is CompleteAll -> this
            is CompleteAllSuccess -> copy(todos = action.todos)
            is CompleteAllError -> copy(error = true)
            is CompleteTodo -> this
            is CompleteTodoSuccess -> copy(todos = action.todos)
            is CompleteTodoError -> copy(error = true)
            is ActivateTodo -> this
            is ActivateTodoSuccess -> copy(todos = action.todos)
            is ActivateTodoError -> copy(error = true)
            is FilterBy -> copy(filter = action.filter)
            is ShowDetail -> copy(navigation = TodoDetail(action.id))
        }
    }

class TodosStore<A : Action, S : StoreState>(
    private val reducer: Reducer<S>,
    middlewares: List<Middleware<A, S>>,
    initialState: S
) : Store<S, A>() {
    private val initialDispatcher =
        Dispatcher<A> { action ->
            state.value = state.value.reduce(action)
            action
        }

    private val dispatcher: Dispatcher<A> =
        middlewares.foldRight(initialDispatcher) { m, acc ->
            Dispatcher { m(this, acc, it) }
        }

    override val state: MutableStateFlow<S> =
        MutableStateFlow(initialState)

    override fun dispatch(action: A) {
        dispatcher(action)
    }

    private fun S.reduce(action: A): S =
        reducer(this, action)
}