package com.acv.mvp.redux

interface Action

object NoAction : Action

fun interface AsyncAction<S : StoreState, A : Action> : Action {
    operator fun invoke(
        state: S,
        dispatcher: Dispatcher<A>
    )
}

//fun interface AsyncActionType<S : StoreState, A : Action> {
//    operator fun invoke(store: Store<S, A>)
//}

inline fun <reified S : StoreState, A : Action> AsyncAction(
    reducer: AsyncAction<S, A>
): AsyncAction<S, A> =
    AsyncAction { state, dispatcher ->
        when {
            state is S -> reducer(state = state, dispatcher = dispatcher)
            else -> NoAction
        }
    }

fun interface Dispatcher<A : Action> {
    operator fun invoke(action: A): A
}