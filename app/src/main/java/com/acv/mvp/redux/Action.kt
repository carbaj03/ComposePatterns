package com.acv.mvp.redux

interface Action

object NoAction : Action

fun interface AsyncAction<S : StoreState> : Action {
    operator fun invoke(
        state: S,
        dispatcher: Dispatcher,
    )
}

fun interface Dispatcher {
    operator fun invoke(action: Action): Action
}