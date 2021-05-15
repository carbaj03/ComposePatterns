package com.acv.mvp.redux

import com.acv.mvp.presentation.Store
import com.acv.mvp.presentation.StoreState

fun interface SideEffect<A : Action, S : StoreState> {
    operator fun invoke(action: A, store: Store<S, A>): A
}