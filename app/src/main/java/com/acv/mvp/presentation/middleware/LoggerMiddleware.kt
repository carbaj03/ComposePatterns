package com.acv.mvp.presentation.middleware

import android.util.Log
import com.acv.mvp.presentation.TodosState
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.Dispatcher
import com.acv.mvp.redux.Middleware
import com.acv.mvp.redux.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoggerMiddleware(
    override val coroutineContext: CoroutineContext,
) : Middleware<TodosState, Action>, CoroutineScope {
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