package com.acv.mvp.redux

interface Action

fun interface Dispatcher<A : Action> {
    operator fun invoke(action: A): A
}