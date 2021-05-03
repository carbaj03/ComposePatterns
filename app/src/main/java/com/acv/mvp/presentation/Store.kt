package com.acv.mvp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.acv.mvp.domain.Task
import com.acv.mvp.domain.Tasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class Action
object LoadTasks : Action()

data class State(
    val tasks: Tasks,
    val input: String,
)

interface ViewStore {
    fun State.render()
}

@OptIn(ExperimentalCoroutinesApi::class)
class Store() : ViewModel(), ActionHandler {
    private val tasks = Tasks(
        listOf(
            Task(id = 1, task = "Create Todo App"),
            Task(id = 2, task = "Create Post"),
        )
    )

    private val state: MutableStateFlow<State> = MutableStateFlow(State(tasks, ""))

    fun init(): StateFlow<State> {
        onInit()
        return state
    }

    fun onInit() {
        LoadTasks.handle()
    }

    override fun Action.handle() {
        val newState: State = reduce(state.value)
        newState.state()
    }

    fun Action.reduce(currentState: State): State =
        when (this) {
            LoadTasks -> currentState
        }


    private fun State.state() {
        state.value = this
    }

    fun onCreate(view: ViewStore) {
        viewModelScope.launch(Dispatchers.IO) {
            init().collect { state ->
                withContext(Dispatchers.Main.immediate) {
                    view.run { state.render() }
                }
            }
        }
    }
}

interface ActionHandler {
    fun Action.handle()
}

interface EmptyState {
    fun empty(): State
}