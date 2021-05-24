package com.acv.mvp.redux

fun interface Middleware<S : StoreState> {
    operator fun invoke(store: Store<S>, next: Dispatcher, action: Action): Action
}