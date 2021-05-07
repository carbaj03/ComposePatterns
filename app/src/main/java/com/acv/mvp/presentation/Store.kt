package com.acv.mvp.presentation

import androidx.lifecycle.ViewModel
import com.acv.mvp.domain.Task
import com.acv.mvp.domain.Tasks
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow

sealed class Action
object LoadTasks : Action()
data class AddTask(val task: String) : Action()
data class ChangeInput(val input: String) : Action()

sealed class State {
    fun state(f: (Success) -> State) =
        if (this is Success) f(this) else f(Success(Tasks(mutableListOf()), ""))
}

object Error : State()
object Loading : State()
data class Success(
    val tasks: Tasks,
    val input: String,
) : State()

@OptIn(ExperimentalCoroutinesApi::class)
class Store() : ViewModel() {
    private val tasks = Tasks(
        listOf(
            Task(id = 1, task = "Create Todo App"),
            Task(id = 2, task = "Create Post"),
        )
    )

    val state: MutableStateFlow<State> = MutableStateFlow(Loading)

    fun Action.reduce(currentState: State): State =
        when (this) {
            is LoadTasks -> currentState.state {
                it.copy(
                    tasks = tasks
                )
            }
            is ChangeInput -> currentState.state {
                it.copy(
                    input = input
                )
            }
            is AddTask ->
                if (task.isEmpty()) Error
                else
                    currentState.state {
                        it.copy(
                            tasks = it.tasks.copy(
                                it.tasks.tasks.plus(
                                    Task(
                                        id = it.tasks.tasks.size + 1,
                                        task = task,
                                    )
                                )
                            ),
                            input = "",
                        )
                    }
        }

    fun action(action: Action) {
        state.value = action.reduce(state.value)
    }
}