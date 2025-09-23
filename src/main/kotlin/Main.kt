package org.example

import arrow.core.getOrElse
import org.example.greetings.Greetings

fun main() {
    val knownNames = setOf("John", "Kim", "Alice", "Frank")

    Greetings(knownNames)
        .greet("Kim")
        .onRight { println(it) }
        .getOrElse { throw IllegalStateException("Unknown name: $it") }
}