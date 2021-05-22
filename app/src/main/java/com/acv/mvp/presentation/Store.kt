package com.acv.mvp.presentation

import com.acv.mvp.redux.Action
import com.acv.mvp.redux.Dispatcher
import com.acv.mvp.redux.Store
import com.acv.mvp.redux.StoreState
import kotlinx.coroutines.flow.StateFlow

class TodosStore<S : StoreState, A : Action>(
    override var dispatch: Dispatcher<A>,
    override val state: StateFlow<S>,
) : Store<S, A>()