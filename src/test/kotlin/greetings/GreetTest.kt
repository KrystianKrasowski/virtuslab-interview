package org.example.greetings

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class GreetTest {

    private val greetings = Greetings(setOf("John", "Kim", "Alice", "Frank"))

    @ParameterizedTest
    @ValueSource(strings = ["John", "Kim", "Alice", "Frank"])
    fun `should greet`() {
        val greetings = greetings.greet("John").getOrNull()
        Assertions.assertEquals("Hello, John!", greetings)
    }

    @Test
    fun `should fail on unknown name`() {
        val problem = greetings.greet("Bruce").leftOrNull()
        Assertions.assertEquals(GreetingsProblem.UnknownName("Bruce"), problem)
    }
}