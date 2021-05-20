package com.acv.mvp.presentation.middleware

import com.acv.mvp.data.Repository
import com.acv.mvp.presentation.GetTodoError
import com.acv.mvp.presentation.GetTodoSuccess
import com.acv.mvp.presentation.TodosState
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.AsyncAction
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class TodoDetailMiddleware(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : CoroutineScope {
    fun GetTodo(id: Int): AsyncAction<TodosState, Action> =
        AsyncAction { state, dispatch ->
            launch {
                val todo: Todo? = repository.getBy(id)
                todo?.let { dispatch(GetTodoSuccess(todo)) }
                    ?: dispatch(GetTodoError)
            }
        }
}