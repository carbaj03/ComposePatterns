package com.acv.mvp.redux

import com.acv.mvp.presentation.StoreState


fun interface Reducer<S : StoreState, A : Action> {
    operator fun invoke(state: S, action: A): S
}

fun <S : StoreState, A : Action> combineReducers(vararg reducers: Reducer<S, A>): Reducer<S, A> =
    Reducer { state, action ->
        reducers.fold(state, { s, reducer -> reducer(s, action) })
    }

operator fun <S : StoreState, A : Action> Reducer<S, A>.plus(other: Reducer<S, A>): Reducer<S, A> =
    Reducer { s, a ->
        other(this(s, a), a)
    }