package com.acv.mvp.presentation

import com.acv.mvp.ui.compose.Filter
import com.acv.mvp.ui.compose.Todo

data class TodosState(
    val navigation: Navigation,
    val todos: List<Todo>,
    val input: String,
    val filter: Filter,
    val detail: Todo?,
) : StoreState {
    companion object {
        fun initalState() = TodosState(
            navigation = TodoList,
            todos = emptyList(),
            input = "",
            filter = Filter.All,
            detail = null,
        )
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