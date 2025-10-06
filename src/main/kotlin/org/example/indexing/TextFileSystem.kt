package org.example.indexing

interface TextFileSystem {

    data class File(val name: String, val content: String)

    fun getFileNames(): Set<String>

    fun getFileContent(name: String): String?

    fun addFile(file: File)

    fun removeFile(fileName: String)
}