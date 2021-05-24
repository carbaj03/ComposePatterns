package com.acv.mvp.ui.compose

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.acv.mvp.data.Repository
import com.acv.mvp.presentation.StoreEnhancer
import com.acv.mvp.presentation.TodosStore
import com.acv.mvp.presentation.middleware.TodoAsyncAction
import com.acv.mvp.presentation.middleware.TodoDetailMiddleware
import com.acv.mvp.redux.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

class StoreFactory<S : StoreState>(
    val reducer: Reducer<S>,
    val initialState: S,
    val enhancer: StoreEnhancer<S>?,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodosStore::class.java)) {
            return createStore(reducer, initialState, enhancer) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

fun <S : StoreState> createStore(
    reducer: Reducer<S>,
    preloadedState: S,
    enhancer: StoreEnhancer<S>? = null,
): Store<S> {
    if (enhancer != null) {
        return enhancer { r, initialState, _ -> createStore(r, initialState) }(
            reducer,
            preloadedState,
            null
        )
    }

    val currentState = MutableStateFlow(preloadedState)

    val dispatcher = Dispatcher { action ->
        currentState.value = reducer(currentState.value, action)
        action
    }

    return object : Store<S> {
        override var dispatch: Dispatcher = dispatcher
        override val state: StateFlow<S> = currentState
    }
}


interface StoreProvider<S : StoreState> {
    @Composable
    operator fun invoke(content: @Composable () -> Unit)

    @Composable
    fun <A> useSelector(f: S.() -> A): State<A>

    @Composable
    fun useDispatch(): State<(Action) -> Unit>
}

fun <S : StoreState> provide(store: Store<S>): StoreProvider<S> {
    val LocalStore: ProvidableCompositionLocal<Store<S>> =
        staticCompositionLocalOf { store }

    return object : StoreProvider<S> {
        @Composable
        override fun invoke(content: @Composable () -> Unit) =
            CompositionLocalProvider(
                LocalStore provides store
            ) {
                content()
            }

        @Composable
        override fun <A> useSelector(f: S.() -> A): State<A> {
            val store: Store<S> = LocalStore.current
            val selector: Flow<A> = store.state.map { f(it) }
            return selector.collectAsState(f(store.state.value))
        }

        @Composable
        override fun useDispatch(): State<(Action) -> Unit> {
            val store: Store<S> = LocalStore.current
            return remember { mutableStateOf({ action: Action -> store.dispatch(action) }) }
        }
    }
}

val TodoThunks = TodoAsyncAction(
    repository = Repository,
    coroutineContext = Dispatchers.IO + SupervisorJob(),
)

val TodoDetailThunks = TodoDetailMiddleware(
    repository = Repository,
    coroutineContext = Dispatchers.IO + SupervisorJob(),
)