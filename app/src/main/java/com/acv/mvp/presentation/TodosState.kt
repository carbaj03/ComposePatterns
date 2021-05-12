package com.acv.mvp.presentation

import com.acv.mvp.ui.compose.Filter
import com.acv.mvp.ui.compose.Todo

data class TodosState(
    val todos: List<Todo>,
    val input: String,
    val input2: String,
    val filter: Filter,
) {
    companion object {
        fun empty() = TodosState(emptyList(), "", "", Filter.All)
    }

    fun itemsLeft(): Int =
        todos.count { !it.completed }

    fun filterBy() =
        when (filter) {
            Filter.All -> todos
            Filter.Active -> todos.filterNot { it.completed }
            Filter.Completed -> todos.filter { it.completed }
        }
}