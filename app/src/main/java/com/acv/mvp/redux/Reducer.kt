package com.acv.mvp.redux

fun interface Reducer<S : StoreState> {
    operator fun invoke(state: S, action: Action): S
}

fun interface ReducerType<S : StoreState, A : Action> {
    operator fun S.invoke(action: A): S
}

inline fun <S : StoreState, reified A : Action> Reducer(
    reducer: ReducerType<S, A>
): Reducer<S> =
    Reducer { state, action ->
        when (action) {
            is A -> reducer.run { state(action) }
            else -> state
        }
    }

fun <S : StoreState> combineReducers(vararg reducers: Reducer<S>): Reducer<S> =
    Reducer { state, action ->
        reducers.fold(state, { s, reducer -> reducer(s, action) })
    }

operator fun <S : StoreState> Reducer<S>.plus(other: Reducer<S>): Reducer<S> =
    Reducer { state, action ->
        other(this(state, action), action)
    }