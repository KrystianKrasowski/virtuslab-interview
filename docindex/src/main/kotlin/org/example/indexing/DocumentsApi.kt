package org.example.indexing

// Maybe this should be split into commands and queries, but we keep things simple for now
interface DocumentsApi {

    fun findFileNames(word: String): Set<String>

    fun countIndexedWords(): Int

    fun listIndexedFileNames(): Set<String>

    fun listIndexedWords(): Set<String>

    fun register(file: FileSystemSpi.File): DocumentsServiceResult

    fun remove(fileName: String): DocumentsServiceResult

    companion object {

        fun create(fileSystem: FileSystemSpi): DocumentsApi =
            DocumentsService(fileSystem)
    }
}