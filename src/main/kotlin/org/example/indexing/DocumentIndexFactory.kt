package org.example.indexing

internal class DocumentIndexFactory(private val fileSystem: TextFileSystem) {

    private val index = mutableMapOf<String, MutableSet<String>>()

    fun create(): DocumentIndex {
        fileSystem.getFileNames()
            .forEach { populateIndex(it) }

        return DocumentIndex(index)
    }

    fun create(fileName: String): DocumentIndex {
        populateIndex(fileName)
        return DocumentIndex(index)
    }

    private fun populateIndex(fileName: String) {
        fileSystem.getFileContent(fileName)
            ?.toWordsSet()
            ?.forEach { index.computeIfAbsent(it) { mutableSetOf() }.add(fileName) }
    }

    private fun String.toWordsSet() =
        split(" ")
            .map { it.lowercase() }
            .toSet()
}