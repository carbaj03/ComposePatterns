package com.acv.mvp.presentation

import com.acv.mvp.redux.Reducer

val TodoReducer: Reducer<TodosState> =
    Reducer<TodosState, TodoListAction> { action ->
        when (action) {
            is LoadTodos -> this
            is LoadTodosSuccess -> copy(todos = action.todos)
            is LoadTodosError -> copy(error = true)
            is AddTodo -> this
            is AddTodoError -> copy(error = true)
            is AddTodoSuccess -> copy(todos = action.todos)
            is InputChange -> copy(input = action.text)
            is ClearCompleted -> this
            is ClearCompletedSuccess -> copy(todos = action.todos)
            is ClearCompletedError -> copy(error = true)
            is CompleteAll -> this
            is CompleteAllSuccess -> copy(todos = action.todos)
            is CompleteAllError -> copy(error = true)
            is CompleteTodo -> this
            is CompleteTodoSuccess -> copy(todos = action.todos)
            is CompleteTodoError -> copy(error = true)
            is ActivateTodo -> this
            is ActivateTodoSuccess -> copy(todos = action.todos)
            is ActivateTodoError -> copy(error = true)
            is FilterBy -> copy(filter = action.filter)
//            is ShowDetail -> copy(navigation = TodoDetail(action.id))
        }
    }

//val NavigationReducer: Reducer<TodosState> =
//    Reducer<TodosState, Navigation> { action ->
//        when (action) {
//            is TodoDetail -> copy(currentTodo = action.id, navigation = TodoDetail(action.id))
//            TodoList -> copy(navigation = TodoList)
//        }
//    }
