package org.example.indexing

import java.io.IOException

class TextFileSystemInMemory(
    private val files: MutableMap<String, TextFileSystem.File>,
    private val failingOnGetFileNames: Boolean = false,
    private val failingOnGetFileByName: Boolean = false,
    private val failingOnAddFile: Boolean = false,
    private val failingOnRemoveFile: Boolean = false,
) : TextFileSystem {

    override fun getFileNames(): Set<String> {
        if (failingOnGetFileNames) {
            throw IOException("Cannot get file names")
        }

        return files.keys
    }

    override fun getFileByName(name: String): TextFileSystem.File? {
        if (failingOnGetFileByName) {
            throw IOException("Cannot get file")
        }

        return files[name]
    }

    override fun addFile(file: TextFileSystem.File) {
        if (failingOnAddFile) {
            throw IOException("Cannot add file")
        }

        files[file.name] = file
    }

    override fun removeFile(fileName: String) {
        if (failingOnRemoveFile) {
            throw IOException("Cannot remove file")
        }

        files.remove(fileName)
    }

    companion object {

        fun ofDefault() =
            TextFileSystemInMemory(
                mutableMapOf(
                    "doc-1.txt" to TextFileSystem.File("doc-1.txt", "Quick brown fox jumps over the lazy dog"),
                    "doc-2.txt" to TextFileSystem.File("doc-2.txt", "Lorem ipsum dolor est sit amet"),
                    "doc-3.txt" to TextFileSystem.File("doc-3.txt", "My cat is very quick")
                )
            )
    }
}