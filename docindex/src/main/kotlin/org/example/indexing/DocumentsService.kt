package org.example.indexing

sealed interface DocumentsServiceResult {
    object Success : DocumentsServiceResult
    object NoOperation : DocumentsServiceResult
    data class Failure(val message: String) : DocumentsServiceResult
}

internal class DocumentsService(private val fileSystem: TextFileSystem) {

    private val documentIndexFactory = DocumentIndexFactory(fileSystem)
    private var index = documentIndexFactory.create()

    fun findFileNames(word: String): Set<String> =
        index.find(word)

    fun register(file: TextFileSystem.File): DocumentsServiceResult {
        if (file.indexable) {
            val delta = documentIndexFactory.create(file)
            return runCatching { fileSystem.addFile(file) }
                .onSuccess { index += delta }
                .map { DocumentsServiceResult.Success }
                .getOrElse { DocumentsServiceResult.Failure(it.message ?: "Cannot register file") }
        } else {
            return DocumentsServiceResult.NoOperation
        }
    }

    fun add(file: TextFileSystem.File) {
        if (file.indexable) {
            val delta = documentIndexFactory.create(file)
            fileSystem.addFile(file)
            index += delta
        }
    }

    fun remove(fileName: String) {
        if (fileName.isNotBlank()) {
            val delta = documentIndexFactory.create(fileName)
            fileSystem.removeFile(fileName)
            index -= delta
        }
    }

    fun countIndexedWords(): Int {
        return index.countWords()
    }

    fun listIndexedFileNames(): Set<String> {
        return index.listFileNames()
    }
}