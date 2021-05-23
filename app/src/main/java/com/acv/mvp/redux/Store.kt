package com.acv.mvp.redux

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class Store<S : StoreState, A : Action> : ViewModel() {
    abstract var dispatch: Dispatcher<A>
    abstract val state: StateFlow<S>
}

//interface StoreCreator<S : StoreState, A : Action> {
//    val reducer: Reducer<S>
//    val initialState: S
//    val enhancer: StoreEnhancer<S, A>?
//}