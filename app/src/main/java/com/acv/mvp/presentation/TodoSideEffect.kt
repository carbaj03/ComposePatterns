package com.acv.mvp.presentation

import android.util.Log
import com.acv.mvp.data.Repository
import com.acv.mvp.redux.SideEffect
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TodoSideEffect(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : SideEffect<TodoAction, TodosState>, CoroutineScope {
    override fun invoke(
        action: TodoAction,
        store: Store<TodosState, TodoAction>
    ): TodoAction {
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
            }
        }
        return action
    }
}

class TodoDetailSideEffect(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : SideEffect<TodoAction, TodosState>, CoroutineScope {
    override fun invoke(
        action: TodoAction,
        store: Store<TodosState, TodoAction>
    ): TodoAction {
        launch {
            when (action) {
                is GetTodo -> {
                    val todo: Todo? = repository.getBy(action.id)
                    todo?.let { store.dispatch(GetTodoSuccess(todo)) }
                        ?: store.dispatch(GetTodoError)
                }
            }
        }
        return action
    }
}

class LoggerSideEffect(
    override val coroutineContext: CoroutineContext,
) : SideEffect<TodoAction, TodosState>, CoroutineScope {
    override fun invoke(
        action: TodoAction,
        store: Store<TodosState, TodoAction>
    ): TodoAction {
        launch {
//            Log.e("oldState", store.state.value.toString())
            Log.e("logger", action.toString())
        }
        return action
    }
}
