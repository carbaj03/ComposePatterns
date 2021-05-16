package com.acv.mvp.data

import com.acv.mvp.presentation.update
import com.acv.mvp.ui.compose.Todo
import kotlinx.coroutines.delay

object Repository {
    private var todos: List<Todo> =
        listOf(
            Todo(id = 0, text = "Start", completed = false)
        )

    suspend fun getAll(): List<Todo>? =
        todos

    suspend fun getBy(id: Int): Todo? {
        delay(3000)
        return todos.firstOrNull { it.id == id }
    }

    suspend fun put(todo: Todo): List<Todo>? {
        delay(500)
        todos = todos.plus(todo)
        return todos
    }

    suspend fun completeTodo(completedId: Int): List<Todo>? {
        delay(3000)
        todos = todos.update(
            condition = { id == completedId },
            transform = { copy(completed = true) }
        )
        return todos
    }
}