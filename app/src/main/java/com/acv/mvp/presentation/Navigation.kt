package com.acv.mvp.presentation

import com.acv.mvp.redux.Action

sealed class Navigation : Action
object TodoList : Navigation()
data class TodoDetail(val id: Int) : Navigation()