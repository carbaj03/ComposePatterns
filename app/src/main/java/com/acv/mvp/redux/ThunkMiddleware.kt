package com.acv.mvp.redux

fun <S : StoreState, A : Action> ThunkMiddleware(): Middleware<S, A> =
    Middleware { store, next, action ->
        if (action is AsyncAction<*, *>) {
            (action as AsyncAction<S, A>)(
                state = store.state.value,
                dispatcher = next
            )
            NoAction as A
        } else {
            next(action)
        }
    }