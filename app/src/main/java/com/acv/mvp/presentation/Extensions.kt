package com.acv.mvp.presentation


inline fun <A> Iterable<A>.update(
    condition: A.() -> Boolean,
    transform: A.() -> A
): List<A> =
    map {
        if (condition(it)) transform(it)
        else it
    }