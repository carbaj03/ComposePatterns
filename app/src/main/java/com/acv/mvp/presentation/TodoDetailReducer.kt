package com.acv.mvp.presentation

import com.acv.mvp.redux.Reducer

val TodoDetailReducer: Reducer<TodosState> =
    Reducer<TodosState, TodoDetailAction> { action ->
        when (action) {
            is GetTodo -> copy(loading = true)
            is GetTodoSuccess -> copy(detail = action.todo, loading = false)
            is GetTodoError -> copy(error = true, loading = false)
            is ShowTodos -> copy(navigation = TodoList, error = false, loading = false)
        }
    }