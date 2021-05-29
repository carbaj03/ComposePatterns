package com.acv.mvp.presentation

import com.acv.mvp.redux.StoreState
import com.acv.mvp.ui.compose.Filter
import com.acv.mvp.ui.compose.Todo

data class TodosState(
//    val navigation: Navigation,
    val todos: List<Todo>,
    val input: String,
    val filter: Filter,
//    val currentTodo: Int?,
    val error: Boolean,
    val loading: Boolean,
) : StoreState {
    companion object {
        fun initialState() = TodosState(
//            navigation = TodoList,
            todos = emptyList(),
            input = "",
            filter = Filter.All,
//            currentTodo = null,
            error = false,
            loading = false,
        )
    }

    fun getTodo(id: Int): Todo? =
        todos.firstOrNull { it.id == id }

    fun itemsLeft(): Int =
        todos.count { !it.completed }

    fun filterBy() =
        when (filter) {
            Filter.All -> todos
            Filter.Active -> todos.filterNot { it.completed }
            Filter.Completed -> todos.filter { it.completed }
        }
}