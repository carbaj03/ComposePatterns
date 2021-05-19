package com.acv.mvp.presentation

import com.acv.mvp.redux.*
import kotlinx.coroutines.flow.MutableStateFlow

class TodosStore<S : StoreState, A : Action>(
    private val reducer: Reducer<S>,
    middlewares: List<Middleware<S, A>>,
    initialState: S,
) : Store<S, A>() {
    private val initialDispatcher =
        Dispatcher<A> { action ->
            state.value = state.value.reduce(action)
            action
        }

    private val dispatcher: Dispatcher<A> =
        middlewares.foldRight(initialDispatcher) { middleware, dispatcher ->
            Dispatcher { middleware(this, dispatcher, it) }
        }

    override val state: MutableStateFlow<S> =
        MutableStateFlow(initialState)

    override fun dispatch(action: A) {
        dispatcher(action)
    }

    private fun S.reduce(action: A): S =
        reducer(this, action)
}