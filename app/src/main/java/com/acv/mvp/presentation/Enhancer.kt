package com.acv.mvp.presentation

import com.acv.mvp.redux.*

fun interface StoreEnhancer<S : StoreState> {
    operator fun invoke(creator: StoreCreator<S>): StoreCreator<S>
}

fun interface StoreCreator<S : StoreState> {
    operator fun invoke(
        reducer: Reducer<S>,
        preloadedState: S,
        enhancer: StoreEnhancer<S>?
    ): Store<S>
}

fun <S : StoreState> applyMiddleware(vararg middlewares: Middleware<S>): StoreEnhancer<S> =
    StoreEnhancer { storeCreator ->
        StoreCreator { reducer, initialState, en ->
            val store = storeCreator(reducer, initialState, en)
            val origDispatch = store.dispatch
            store.dispatch = middlewares.foldRight(origDispatch) { middleware, dispatcher ->
                Dispatcher { middleware(store, dispatcher, it) }
            }
            store
        }
    }

fun <S : StoreState> applyA(): StoreEnhancer<S> =
    StoreEnhancer { storeCreator ->
        StoreCreator { reducer, initialState, en ->
            val store = storeCreator(reducer, initialState, en)
            store
        }
    }