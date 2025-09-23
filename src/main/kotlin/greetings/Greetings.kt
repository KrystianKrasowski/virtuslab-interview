package org.example.greetings

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class Greetings(val knownNames: Set<String>) {

    fun greet(name: String): Either<GreetingsProblem, String> =
        "Hello, $name!"
            .takeIf { knownNames.contains(name) }
            ?.right()
            ?: GreetingsProblem.UnknownName(name).left()
}