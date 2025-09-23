package org.example.greetings

sealed interface GreetingsProblem {

    data class UnknownName(val name: String) : GreetingsProblem
}