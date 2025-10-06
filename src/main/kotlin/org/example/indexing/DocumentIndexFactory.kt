package org.example.indexing

private typealias MutableDocumentIndex = MutableMap<String, MutableSet<String>>

private fun mutableDocumentIndexOf() = mutableMapOf<String, MutableSet<String>>()

internal class DocumentIndexFactory(private val fileSystem: TextFileSystem) {

    fun create(): DocumentIndex {
        val index = mutableDocumentIndexOf()

        fileSystem.getFileNames()
            .forEach { index.populateBy(it) }

        return DocumentIndex(index)
    }

    fun create(fileName: String): DocumentIndex =
        mutableDocumentIndexOf()
            .populateBy(fileName)
            .let { DocumentIndex(it) }

    fun create(file: TextFileSystem.File): DocumentIndex =
        mutableDocumentIndexOf()
            .populateBy(file)
            .let { DocumentIndex(it) }

    private fun MutableDocumentIndex.populateBy(fileName: String) = apply {
        fileSystem.getFileByName(fileName)
            ?.let { populateBy(it) }
    }

    private fun MutableDocumentIndex.populateBy(file: TextFileSystem.File) = apply {
        file.content
            .trim()
            .takeIf { it.isNotEmpty() }
            ?.toWordsSet()
            ?.forEach { addFileName(it, file.name) }
    }

    private fun MutableDocumentIndex.addFileName(word: String, fileName: String) {
        computeIfAbsent(word) { mutableSetOf() }.add(fileName)
    }

    // TODO: Maybe this should be more sophisticated words recognition?
    private fun String.toWordsSet() =
        split(" ")
            .map { it.lowercase() }
            .toSet()
}