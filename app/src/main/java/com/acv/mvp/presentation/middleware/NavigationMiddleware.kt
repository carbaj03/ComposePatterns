package com.acv.mvp.presentation.middleware

import com.acv.mvp.presentation.TodosState
import com.acv.mvp.redux.Action
import com.acv.mvp.redux.Middleware

fun NavigationMiddleware(navigator: Navigator): Middleware<TodosState> =
    Middleware { store, next, action ->
        when (action) {
            is NavigateTo -> navigator.goTo(action.screen)
//            is ShowDetail -> navigator.goTo(Screen.TodoDetail(action.id))
            is GoBack -> navigator.goBack()
        }
        next(action)
    }

sealed class Screen {
    object TodoList : Screen()
    data class TodoDetail(
        val id: Int
    ) : Screen()
}

interface Navigator {
    fun goTo(screen: Screen)
    fun goBack()
}

data class NavigateTo(val screen: Screen) : Action
object GoBack : Action