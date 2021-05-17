package com.acv.mvp.redux

import com.acv.mvp.presentation.Store
import com.acv.mvp.presentation.StoreState

fun interface Middleware<A : Action, S : StoreState> {
    operator fun invoke(store: Store<S, A>, next: Dispatcher<A>, action: A): A
}