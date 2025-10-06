package org.example.indexing

class DocumentsService(private val fileSystem: TextFileSystem) {

    private var index = DocumentIndexFactory(fileSystem).create()

    fun findFileNames(word: String): Set<String> =
        index.find(word)

    fun add(file: TextFileSystem.File) {
        fileSystem.addFile(file)
        val delta = DocumentIndexFactory(fileSystem).create(file.name)
        index = index.append(delta)
    }

    fun remove(fileName: String) {
        val delta = DocumentIndexFactory(fileSystem).create(fileName)
        index = index.remove(delta)
        fileSystem.removeFile(fileName)
    }
}