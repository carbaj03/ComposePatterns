package com.acv.mvp.redux

fun <S : StoreState> ThunkMiddleware(): Middleware<S> =
    Middleware { store, next, action ->
        if (action is AsyncAction<*>) {
            (action as AsyncAction<S>)(
                state = store.state.value,
                dispatcher = next
            )
            NoAction
        } else {
            next(action)
        }
    }