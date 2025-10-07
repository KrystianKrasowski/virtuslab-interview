package org.example.indexing

internal class DocumentsService(private val fileSystem: TextFileSystem) {

    private val documentIndexFactory = DocumentIndexFactory(fileSystem)
    private var index = documentIndexFactory.create()

    fun findFileNames(word: String): Set<String> =
        word.toIndexableWord()
            ?.let { index.find(it) }
            ?: emptySet()

    fun register(file: TextFileSystem.File): DocumentsServiceResult {
        if (!file.indexable) {
            return DocumentsServiceResult.NoOperation
        }

        val delta = documentIndexFactory.create(file)

        return runCatching { fileSystem.addFile(file) }
            .onSuccess { index += delta }
            .map { DocumentsServiceResult.Success }
            .getOrElse { DocumentsServiceResult.Failure(it.message ?: "Cannot register file") }
    }

    fun remove(fileName: String): DocumentsServiceResult {
        if (fileName.isBlank()) {
            return DocumentsServiceResult.NoOperation
        }

        val delta = documentIndexFactory.create(fileName)

        if (delta.isEmpty()) {
            return DocumentsServiceResult.NoOperation
        }

        return runCatching { fileSystem.removeFile(fileName) }
            .onSuccess { index -= delta }
            .map { DocumentsServiceResult.Success }
            .getOrElse { DocumentsServiceResult.Failure(it.message ?: "Cannot remove document") }
    }

    fun countIndexedWords(): Int {
        return index.countWords()
    }

    fun listIndexedFileNames(): Set<String> {
        return index.listFileNames()
    }

    fun listIndexedWords(): Set<String> {
        return index.listWords()
    }
}