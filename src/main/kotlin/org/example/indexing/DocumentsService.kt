package org.example.indexing

class DocumentsService(private val fileSystem: TextFileSystem) {

    private var index = DocumentIndexFactory(fileSystem).create()

    fun findFileNames(word: String): Set<String> =
        index.find(word)

    fun add(file: TextFileSystem.File) {
        fileSystem
            .runCatching { addFile(file) }
            .mapCatching { DocumentIndexFactory(fileSystem).create(file.name) }
            .onSuccess { index += it }
            .onFailure { throw it }
    }

    fun remove(fileName: String) {
        DocumentIndexFactory(fileSystem)
            .runCatching { create(fileName) }
            .onSuccess { index -= it }
            .onSuccess { fileSystem.removeFile(fileName) }
            .onFailure { throw it }
    }
}