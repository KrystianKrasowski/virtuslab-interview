package org.example.indexing

class TextFileSystemInMemory(private val files: MutableMap<String, TextFileSystem.File>) : TextFileSystem {

    override fun getFileNames(): Set<String> {
        return files.keys
    }

    override fun getFileContent(name: String): String? {
        return files[name]?.content
    }

    override fun addFile(file: TextFileSystem.File) {
        files[file.name] = file
    }

    override fun removeFile(fileName: String) {
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