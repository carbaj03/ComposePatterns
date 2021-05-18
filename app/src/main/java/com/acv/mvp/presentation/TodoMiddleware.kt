package com.acv.mvp.presentation

import android.util.Log
import com.acv.mvp.data.Repository
import com.acv.mvp.redux.*
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TodoListMiddleware(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : Middleware<Action, TodosState>, CoroutineScope {
    override fun invoke(
        store: Store<TodosState, Action>,
        next: Dispatcher<Action>,
        action: Action,
    ): Action {
        launch {
            when (action) {
                is LoadTodos -> {
                    val todos = repository.getAll()
                    todos?.let { store.dispatch(LoadTodosSuccess(todos)) }
                        ?: store.dispatch(LoadTodosError)
                }
                is AddTodo -> {
                    val todos = repository.put(Todo(store.state.value.todos.size + 1, action.text, false))
                    todos?.let { store.dispatch(AddTodoSuccess(todos)) }
                        ?: store.dispatch(AddTodoError)
                }
                is CompleteTodo -> {
                    val todos: List<Todo>? = repository.completeTodo(action.selectedId)
                    todos?.let { store.dispatch(CompleteTodoSuccess(todos)) }
                        ?: store.dispatch(CompleteTodoError)
                }
                is CompleteAll -> {
                    val todos: List<Todo>? = repository.completeAll()
                    todos?.let { store.dispatch(CompleteAllSuccess(todos)) }
                        ?: store.dispatch(CompleteAllError)
                }
                is ClearCompleted -> {
                    val todos: List<Todo>? = repository.clearCompleted()
                    todos?.let { store.dispatch(ClearCompletedSuccess(todos)) }
                        ?: store.dispatch(ClearCompletedError)
                }
                is ActivateTodo -> {
                    val todos: List<Todo>? = repository.activateTodo(action.activatedId)
                    todos?.let { store.dispatch(ActivateTodoSuccess(todos)) }
                        ?: store.dispatch(ActivateTodoError)
                }
            }
        }
        return next(action)
    }
}

class TodoDetailMiddleware(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : Middleware<Action, TodosState>, CoroutineScope {
    override fun invoke(
        store: Store<TodosState, Action>,
        next: Dispatcher<Action>,
        action: Action,
    ): Action {
        launch {
            when (action) {
                is
                is GetTodo -> {
                    val todo: Todo? = repository.getBy(action.id)
                    todo?.let { store.dispatch(GetTodoSuccess(todo)) }
                        ?: store.dispatch(GetTodoError)
                }
            }
        }
        return next(action)
    }
}

class LoggerMiddleware(
    override val coroutineContext: CoroutineContext,
) : Middleware<Action, TodosState>, CoroutineScope {
    override fun invoke(
        store: Store<TodosState, Action>,
        next: Dispatcher<Action>,
        action: Action,
    ): Action {
        launch {
            Log.e("logger", action.toString())
        }
        return next(action)
    }
}

val ThunkMiddleware = Middleware<Action, TodosState> { store, next, action ->
    if (action is AsyncAction) {
        action(state = store.state.value, dispatcher = next)
        NoAction
    } else {
        next(action)
    }
}

class TodoThunks(
    repository: Repository,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    val TodoAll = AsyncAction { _, dispatcher ->
        launch {
            dispatcher(LoadTodos)
            val todos = repository.getAll()
            todos?.let { dispatcher(LoadTodosSuccess(todos)) }
                ?: dispatcher(LoadTodosError)
        }
    }
}