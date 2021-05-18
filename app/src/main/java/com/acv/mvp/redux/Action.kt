package com.acv.mvp.redux

import com.acv.mvp.presentation.StoreState

interface Action

object NoAction : Action

fun interface AsyncAction : Action {
    operator fun invoke(
        state: StoreState,
        dispatcher: Dispatcher<Action>
    )
}

fun interface Dispatcher<A : Action> {
    operator fun invoke(action: A): A
}