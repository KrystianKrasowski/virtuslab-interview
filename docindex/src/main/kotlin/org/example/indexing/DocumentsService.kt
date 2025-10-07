package org.example.indexing

internal class DocumentsService(private val fileSystem: FileSystemSpi) : DocumentsApi {

    private val documentIndexFactory = DocumentIndexFactory(fileSystem)
    private var index = documentIndexFactory.create()

    override fun findFileNames(word: String): Set<String> =
        word.toIndexableWord()
            ?.let { index.find(it) }
            ?: emptySet()

    override fun countIndexedWords(): Int =
        index.countWords()

    override fun listIndexedFileNames(): Set<String> =
        index.listFileNames()

    override fun listIndexedWords(): Set<String> =
        index.listWords()

    override fun register(file: FileSystemSpi.File): DocumentsServiceResult {
        if (!file.indexable) {
            return DocumentsServiceResult.NoOperation
        }

        val delta = documentIndexFactory.create(file)

        return runCatching { fileSystem.addFile(file) }
            .onSuccess { index += delta }
            .map { DocumentsServiceResult.Success }
            .getOrElse { DocumentsServiceResult.Failure(it.message ?: "Cannot register file") }
    }

    override fun remove(fileName: String): DocumentsServiceResult {
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
}