package org.example.indexing

class DocumentsService(private val fileSystem: TextFileSystem) {

    private val documentIndexFactory = DocumentIndexFactory(fileSystem)
    private var index = documentIndexFactory.create()

    fun findFileNames(word: String): Set<String> =
        index.find(word)

    fun add(file: TextFileSystem.File) {
        fileSystem
            .runCatching { addFile(file) }
            .mapCatching { documentIndexFactory.create(file.name) }
            .onSuccess { index += it }
            .onFailure { throw it }
    }

    fun remove(fileName: String) {
        documentIndexFactory
            .runCatching { create(fileName) }
            .mapCatching {
                fileSystem.removeFile(fileName)
                index - it
            }
            .onSuccess { index = it }
            .onFailure { throw it }
    }
}