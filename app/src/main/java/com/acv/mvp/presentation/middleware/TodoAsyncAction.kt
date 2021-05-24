package com.acv.mvp.presentation.middleware

import com.acv.mvp.data.Repository
import com.acv.mvp.presentation.*
import com.acv.mvp.redux.AsyncAction
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TodoAsyncAction(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    fun LoadTodos(): AsyncAction<TodosState> =
        AsyncAction { state, dispatch ->
            launch {
                dispatch(LoadTodos)
                val todos = repository.getAll()
                todos?.let { dispatch(LoadTodosSuccess(todos)) }
                    ?: dispatch(LoadTodosError)
            }
        }

    fun AddTodo(text: String): AsyncAction<TodosState> =
        AsyncAction { state, dispatch ->
            launch {
                val todos = repository.put(
                    Todo(id = state.todos.size + 1, text, completed = false)
                )
                todos?.let { dispatch(AddTodoSuccess(todos)) }
                    ?: dispatch(AddTodoError)
            }
        }

    fun CompleteTodo(selectedId: Int): AsyncAction<TodosState> =
        AsyncAction { state, dispatch ->
            launch {
                val todos = repository.completeTodo(selectedId)
                todos?.let { dispatch(CompleteTodoSuccess(todos)) }
                    ?: dispatch(CompleteTodoError)
            }
        }

    fun CompleteAll(): AsyncAction<TodosState> =
        AsyncAction { state, dispatch ->
            launch {
                val todos: List<Todo>? = repository.completeAll()
                todos?.let { dispatch(CompleteAllSuccess(todos)) }
                    ?: dispatch(CompleteAllError)
            }
        }

    fun ClearCompleted(): AsyncAction<TodosState> =
        AsyncAction { state, dispatch ->
            launch {
                val todos: List<Todo>? = repository.clearCompleted()
                todos?.let { dispatch(ClearCompletedSuccess(todos)) }
                    ?: dispatch(ClearCompletedError)
            }
        }

    fun ActivateTodo(activatedId: Int): AsyncAction<TodosState> =
        AsyncAction { state, dispatch ->
            launch {
                val todos: List<Todo>? = repository.activateTodo(activatedId)
                todos?.let { dispatch(ActivateTodoSuccess(todos)) }
                    ?: dispatch(ActivateTodoError)
            }
        }
}