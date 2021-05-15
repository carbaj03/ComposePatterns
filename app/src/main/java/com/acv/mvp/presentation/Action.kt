package com.acv.mvp.presentation

import com.acv.mvp.redux.Action
import com.acv.mvp.ui.compose.Filter
import com.acv.mvp.ui.compose.Todo

sealed class TodoAction : Action
object LoadTodos : TodoAction()
data class LoadTodosSuccess(
    val todos: List<Todo>
) : TodoAction()

data class InputChange(
    val text: String,
) : TodoAction()

data class AddTodo(val text: String) : TodoAction()
object ClearCompleted : TodoAction()
object CompleteAll : TodoAction()
data class CompleteTodo(val selectedId: Int) : TodoAction()
data class ActivateTodo(val selectedId: Int) : TodoAction()
data class FilterBy(val filter: Filter) : TodoAction()
data class ShowDetail(val id: Int) : TodoAction()

sealed class TodoDetailAction : Action
data class GetTodo(val id: Int) : TodoDetailAction()