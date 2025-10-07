package org.example.indexing

internal fun String.toIndexableWord() =
    trim()
        .lowercase()