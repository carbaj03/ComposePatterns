package com.acv.mvp.redux

import kotlinx.coroutines.flow.StateFlow

interface Store<S : StoreState> {
    var dispatch: Dispatcher
    val state: StateFlow<S>
}