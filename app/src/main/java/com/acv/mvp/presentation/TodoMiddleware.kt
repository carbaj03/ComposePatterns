package com.acv.mvp.presentation

import com.acv.mvp.data.Repository
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.Dispatcher
import com.acv.mvp.redux.Middleware
import com.acv.mvp.redux.Store
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


class TodoDetailMiddleware(
    private val repository: Repository,
    override val coroutineContext: CoroutineContext,
) : Middleware<TodosState, Action>, CoroutineScope {
    override fun invoke(
        store: Store<TodosState, Action>,
        next: Dispatcher<Action>,
        action: Action,
    ): Action {
        launch {
            when (action) {
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