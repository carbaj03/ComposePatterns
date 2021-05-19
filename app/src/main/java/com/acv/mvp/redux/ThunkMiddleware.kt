package com.acv.mvp.redux

fun <S : StoreState> ThunkMiddleware(): Middleware<S, Action> =
    Middleware { store, next, action ->
        if (action is AsyncAction<*, *>) {
            (action as AsyncAction<S, Action>)(store)
//            (action as AsyncAction<StoreState,Action>)(state = store.state.value, dispatcher = next)
            NoAction
        } else {
            next(action)
        }
    }