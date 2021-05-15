package com.acv.mvp.presentation

sealed class Navigation
object TodoList : Navigation()
data class TodoDetail(val id: Int) : Navigation()