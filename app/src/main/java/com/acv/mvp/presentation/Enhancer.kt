package com.acv.mvp.presentation

import com.acv.mvp.redux.*

fun interface StoreEnhancer<S : StoreState, A : Action> {
    operator fun invoke(creator: StoreCreator<S, A>): StoreCreator<S, A>
}


fun interface StoreCreator<S : StoreState, A : Action> {
    operator fun invoke(reducer: Reducer<S>, preloadedState: S): Store<S, A>
}


/**
 * Creates a store enhancer that applies middleware to the dispatch method
 * of the Redux store. This is handy for a variety of tasks, such as expressing
 * asynchronous actions in a concise manner, or logging every action payload.
 *
 * See `redux-thunk` package as an example of the Redux middleware.
 *
 * Because middleware is potentially asynchronous, this should be the first
 * store enhancer in the composition chain.
 *
 * Note that each middleware will be given the `dispatch` and `getState` functions
 * as named arguments.
 *
 * @param {vararg Middleware} [middleware] The middleware chain to be applied.
 * @returns {StoreEnhancer} A store enhancer applying the middleware.
 */
fun <S : StoreState, A : Action> applyMiddleware(vararg middlewares: Middleware<S, A>): StoreEnhancer<S, A> =
    StoreEnhancer { storeCreator ->
        StoreCreator { reducer, initialState ->
            val store = storeCreator(reducer, initialState)
            val origDispatch = store.dispatch
            store.dispatch = middlewares.foldRight(origDispatch) { middleware, dispatcher ->
                Dispatcher { middleware(store, dispatcher, it) }
            }
            store
        }
    }
