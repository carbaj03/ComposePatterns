package com.acv.mvp.ui.compose

import androidx.compose.runtime.*
import com.acv.mvp.data.Repository
import com.acv.mvp.presentation.StoreEnhancer
import com.acv.mvp.presentation.middleware.TodoAsyncAction
import com.acv.mvp.presentation.middleware.TodoDetailMiddleware
import com.acv.mvp.redux.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

//class StoreFactory<S : StoreState, A : Action>(
//    override val reducer: Reducer<S>,
//    override val initialState: S,
//    override val enhancer: StoreEnhancer<S, A>?,
//) : ViewModelProvider.Factory, StoreCreator<S, A> {
//    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//        if (modelClass.isAssignableFrom(TodosStore::class.java)) {
//            return createStore(reducer, initialState, enhancer) as T
//        }
//        throw IllegalArgumentException("Unknown ViewModel class")
//    }
//}

fun <S : StoreState, A : Action> createStore(
    reducer: Reducer<S>,
    preloadedState: S,
    enhancer: StoreEnhancer<S, A>? = null,
): Store<S, A> {
    if (enhancer != null) {
        return enhancer { r, initialState, en -> createStore(r, initialState) }(
            reducer,
            preloadedState,
            null
        )
    }

    val currentState = MutableStateFlow(preloadedState)

    val dispatcher = Dispatcher<A> { action ->
        currentState.value = reducer(currentState.value, action)
        action
    }

    return object : Store<S, A>() {
        override var dispatch: Dispatcher<A> = dispatcher
        override val state: StateFlow<S> = currentState
    }
}


internal val LocalStore: ProvidableCompositionLocal<Store<StoreState, Action>> =
    staticCompositionLocalOf {
        throw Exception("Provide a Store")
    }

@Composable
fun <S : StoreState, A : Action> Store(
    store: Store<S, A>,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalStore provides store as Store<StoreState, Action>) {
        content()
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


//val storeFactory: StoreFactory<TodosState, Action> =
//    StoreFactory(
//        reducer = reducers,
//        initialState = TodosState.initalState(),
//        middlewares = middlewares,
//    )

@Composable
fun <S : StoreState, A> useSelector(f: S.() -> A): State<A> {
    val store: Store<S, Action> = LocalStore.current as Store<S, Action>
//    val store: TodosStore<TodosState, TodoAction> = viewModel(factory = storeFactory)
    val selector: Flow<A> = store.state.map { f(it) }
    return selector.collectAsState(f(store.state.value))
}

//@Composable
//fun <A> useSelector(f: TodosState.() -> A) = useSelectorInternal(f)

//@Composable
//fun <A : Action> useDispatch(): State<(A) -> Unit> {
////    val store: TodosStore<TodosState, A> = viewModel(factory = storeFactory)
//    val store: TodosStore<TodosState, Action> = LocalStore.current
//    return remember { mutableStateOf({ action: A -> store.dispatch(action) }) }
//}

@Composable
fun useDispatch(): State<(Action) -> Unit> {
//    val store: TodosStore<TodosState, A> = viewModel(factory = storeFactory)
    val store: Store<StoreState, Action> = LocalStore.current
    return remember { mutableStateOf({ action: Action -> store.dispatch(action) }) }
}
