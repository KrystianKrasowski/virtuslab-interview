package org.example.indexing

internal class DocumentsService(private val fileSystem: TextFileSystem) {

    private val documentIndexFactory = DocumentIndexFactory(fileSystem)
    private var index = documentIndexFactory.create()

    fun findFileNames(word: String): Set<String> =
        index.find(word)

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