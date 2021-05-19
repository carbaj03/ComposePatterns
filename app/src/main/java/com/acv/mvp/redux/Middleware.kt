package com.acv.mvp.redux

fun interface Middleware<S : StoreState, A : Action> {
    operator fun invoke(store: Store<S, A>, next: Dispatcher<A>, action: A): A
}