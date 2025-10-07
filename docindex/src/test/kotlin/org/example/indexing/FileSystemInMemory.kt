package org.example.indexing

import java.io.IOException

class FileSystemInMemory(
    private val files: MutableMap<String, FileSystemSpi.File>,
    private val failingOnGetFileNames: Boolean = false,
    private val failingOnGetFileByName: Boolean = false,
    private val failingOnAddFile: Boolean = false,
    private val failingOnRemoveFile: Boolean = false,
) : FileSystemSpi {

    override fun getFileNames(): Set<String> {
        if (failingOnGetFileNames) {
            throw IOException("Cannot get file names")
        }

        return files.keys
    }

    override fun getFileByName(name: String): FileSystemSpi.File? {
        if (failingOnGetFileByName) {
            throw IOException("Cannot get file")
        }

        return files[name]
    }

    override fun addFile(file: FileSystemSpi.File) {
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
            FileSystemInMemory(
                mutableMapOf(
                    "doc-1.txt" to FileSystemSpi.File("doc-1.txt", "Quick brown fox jumps over the lazy dog"),
                    "doc-2.txt" to FileSystemSpi.File("doc-2.txt", "Lorem ipsum dolor est sit amet"),
                    "doc-3.txt" to FileSystemSpi.File("doc-3.txt", "My cat is very quick")
                )
            )
    }
}