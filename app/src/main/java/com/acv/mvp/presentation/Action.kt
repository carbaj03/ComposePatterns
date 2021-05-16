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

object LoadTodosError : TodoListAction()

data class InputChange(
    val text: String,
) : TodoListAction()

data class AddTodo(val text: String) : TodoListAction()
data class AddTodoSuccess(val todos: List<Todo>) : TodoListAction()
object AddTodoError : TodoListAction()
object ClearCompleted : TodoListAction()
data class ClearCompletedSuccess(val todos: List<Todo>) : TodoListAction()
object ClearCompletedError : TodoListAction()
object CompleteAll : TodoListAction()
data class CompleteAllSuccess(val todos: List<Todo>) : TodoListAction()
object CompleteAllError : TodoListAction()
data class CompleteTodo(val selectedId: Int) : TodoListAction()
data class CompleteTodoSuccess(val todos: List<Todo>) : TodoListAction()
object CompleteTodoError : TodoListAction()
data class ActivateTodo(val selectedId: Int) : TodoListAction()
data class FilterBy(val filter: Filter) : TodoListAction()
data class ShowDetail(val id: Int) : TodoListAction()

sealed class TodoDetailAction : TodoAction()
data class GetTodo(val id: Int) : TodoDetailAction()
data class GetTodoSuccess(val todo: Todo) : TodoDetailAction()
object GetTodoError : TodoDetailAction()
object ShowTodos : TodoDetailAction()