package com.acv.mvp.presentation


inline fun <T, R> Iterable<T>.update(
    condition: T.() -> Boolean,
    transform: T.() -> R
): List<R> =
    map {
        if (condition(it)) transform(it)
        else it as R
    }