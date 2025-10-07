package org.example.indexing

interface FileSystemSpi {

    data class File(val name: String, val content: String) {

        val indexable = name.isNotBlank() && content.isNotBlank()
    }

    fun getFileNames(): Set<String>

    fun getFileByName(name: String): File?

    fun addFile(file: File)

    fun removeFile(fileName: String)
}