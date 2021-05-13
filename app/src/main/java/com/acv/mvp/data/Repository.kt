package com.acv.mvp.data

import com.acv.mvp.ui.compose.Todo

class Repository() {
    private var todos = listOf(
        Todo(id = 0, text = "Start", completed = false)
    )

    suspend fun getAll(): List<Todo> =
        todos

    suspend fun put(todo: Todo): List<Todo> {
        todos = todos.plus(todo)
        return todos
    }
}