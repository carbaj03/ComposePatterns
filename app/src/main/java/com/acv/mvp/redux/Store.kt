package com.acv.mvp.redux

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class Store<S : StoreState, A : Action> : ViewModel() {
    abstract val state: StateFlow<S>
    abstract fun dispatch(action: A)
}

interface StoreCreator<S : StoreState, A : Action> {
    val reducer: Reducer<S>
    val initialState: S
    val middlewares: List<Middleware<S, A>>
}