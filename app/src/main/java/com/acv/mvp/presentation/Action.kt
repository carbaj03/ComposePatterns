package com.acv.mvp.presentation

import com.acv.mvp.ui.compose.Filter
import com.acv.mvp.ui.compose.Todo

sealed class Action
object LoadTodos : Action()
data class LoadTodosSuccess(
    val todos: List<Todo>
) : Action()

data class InputChange(
    val text: String,
) : Action()

data class InputChange2(
    val text: String,
) : Action()

data class AddTodo(val text: String) : Action()
object ClearCompleted : Action()
object CompleteAll : Action()
data class CompleteTodo(val selectedId: Int) : Action()
data class ActivateTodo(val selectedId: Int) : Action()
data class FilterBy(val filter: Filter) : Action()