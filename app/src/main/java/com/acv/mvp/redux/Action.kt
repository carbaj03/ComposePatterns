package com.acv.mvp.redux

interface Action

object NoAction : Action

fun interface AsyncAction<S : StoreState, A : Action> : Action {
    operator fun invoke(
        state: S,
        dispatcher: Dispatcher<A>,
    )
}

//fun interface AsyncActionArgs<S : StoreState, A : Action, D : Argument> : Action {
//    operator fun D.invoke(
//        state: S,
//        dispatcher: Dispatcher<A>,
//    )
//}

//inline fun <S : StoreState, reified A : Action, D : Argument> AsyncAction(
//    d: D,
//    asyncAction: AsyncActionArgs<S, A, D>
//): AsyncAction<S, A> =
//    AsyncAction { state, action ->
//        when (action) {
//            is A -> asyncAction.run { d(state, action) }
//            else -> NoAction
//        }
//    }

fun interface Dispatcher<A : Action> {
    operator fun invoke(action: A): A
}

//interface Argument
//data class TodoArg(
//    val repository: Repository,
//    val coroutineScope: CoroutineScope,
//) : Argument