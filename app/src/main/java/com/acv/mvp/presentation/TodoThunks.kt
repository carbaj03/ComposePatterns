package com.acv.mvp.presentation

import com.acv.mvp.data.Repository
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.AsyncAction
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class TodoThunks(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    fun LoadTodos(): AsyncAction<TodosState, Action> =
        AsyncAction { s, dispatcher ->
            launch {
                dispatcher(LoadTodos)
                val todos = Repository.getAll()
                todos?.let { dispatcher(LoadTodosSuccess(todos)) }
                    ?: dispatcher(LoadTodosError)
            }
        }

    fun AddTodo(text: String): AsyncAction<TodosState, Action> =
        AsyncAction { store ->
            launch {
                val todos = Repository.put(Todo(store.state.value.todos.size + 1, text, false))
                todos?.let { store.dispatch(AddTodoSuccess(todos)) }
                    ?: store.dispatch(AddTodoError)
            }
        }

    fun CompleteTodo(selectedId: Int): AsyncAction<TodosState, Action> =
        AsyncAction { state, dispatch ->
            launch {
                val todos = Repository.completeTodo(selectedId)
                todos?.let { dispatch(CompleteTodoSuccess(todos)) }
                    ?: dispatch(CompleteTodoError)
            }
        }

    fun CompleteAll(): AsyncAction<TodosState, Action> =
        AsyncAction { state, dispatch ->
            launch {
                val todos: List<Todo>? = Repository.completeAll()
                todos?.let { dispatch(CompleteAllSuccess(todos)) }
                    ?: dispatch(CompleteAllError)
            }
        }

    fun ClearCompleted(): AsyncAction<TodosState, Action> =
        AsyncAction { state, dispatch ->
            launch {
                val todos: List<Todo>? = Repository.clearCompleted()
                todos?.let { dispatch(ClearCompletedSuccess(todos)) }
                    ?: dispatch(ClearCompletedError)
            }
        }

    fun ActivateTodo(activatedId: Int): AsyncAction<TodosState, Action> =
        AsyncAction { state, dispatch ->
            launch {
                val todos: List<Todo>? = Repository.activateTodo(activatedId)
                todos?.let { dispatch(ActivateTodoSuccess(todos)) }
                    ?: dispatch(ActivateTodoError)
            }
        }
}