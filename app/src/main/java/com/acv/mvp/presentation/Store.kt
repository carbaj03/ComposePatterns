package com.acv.mvp.presentation

import androidx.lifecycle.ViewModel
import com.acv.mvp.redux.Dispatcher
import com.acv.mvp.redux.Store
import kotlinx.coroutines.flow.StateFlow


class TodosStore(
    override var dispatch: Dispatcher,
    override val state: StateFlow<TodosState>
) : ViewModel(), Store<TodosState>