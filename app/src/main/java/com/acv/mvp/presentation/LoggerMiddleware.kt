package com.acv.mvp.presentation

import android.util.Log
import com.acv.mvp.redux.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class LoggerMiddleware<S : StoreState>(
    override val coroutineContext: CoroutineContext,
) : Middleware<S, Action>, CoroutineScope {
    override fun invoke(
        store: Store<S, Action>,
        next: Dispatcher<Action>,
        action: Action,
    ): Action {
        launch {
            Log.e("logger", action.toString())
        }
        return next(action)
    }
}