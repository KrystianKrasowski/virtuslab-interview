package org.example.indexing

private val NOT_ALLOWED_CHARACTERS = Regex("[^\\p{L}\\p{N} ]+")

internal fun String.toIndexableWord() =
    replace(NOT_ALLOWED_CHARACTERS, "")
        .takeUnless { it.isBlank() }
        ?.trim()
        ?.lowercase()