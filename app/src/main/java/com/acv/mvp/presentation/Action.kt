package com.acv.mvp.presentation

import com.acv.mvp.redux.Action
import com.acv.mvp.ui.compose.Filter
import com.acv.mvp.ui.compose.Todo

sealed class TodoAction : Action

sealed class TodoListAction : TodoAction()
object LoadTodos : TodoListAction()
data class LoadTodosSuccess(
    val todos: List<Todo>
) : TodoListAction()

data class InputChange(
    val text: String,
) : TodoListAction()

data class AddTodo(val text: String) : TodoListAction()
object ClearCompleted : TodoListAction()
object CompleteAll : TodoListAction()
data class CompleteTodo(val selectedId: Int) : TodoListAction()
data class ActivateTodo(val selectedId: Int) : TodoListAction()
data class FilterBy(val filter: Filter) : TodoListAction()
data class ShowDetail(val id: Int) : TodoListAction()

sealed class TodoDetailAction : TodoAction()
data class GetTodo(val id: Int) : TodoDetailAction()