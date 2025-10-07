package org.example.app

import org.example.indexing.DocumentsApi
import org.example.indexing.DocumentsServiceResult
import org.example.indexing.FileSystemSpi
import java.util.Scanner

private class FileSystemInMemory : FileSystemSpi {

    private val files = mutableMapOf(
        "doc-1.txt" to FileSystemSpi.File("doc-1.txt", "Quick brown fox jumps over the lazy dog"),
        "doc-2.txt" to FileSystemSpi.File("doc-2.txt", "Lorem ipsum dolor est sit amet"),
        "doc-3.txt" to FileSystemSpi.File("doc-3.txt", "My cat is very quick")
    )

    override fun getFileNames(): Set<String> {
        return files.keys
    }

    override fun getFileByName(name: String): FileSystemSpi.File? {
        return files[name]
    }

    override fun addFile(file: FileSystemSpi.File) {
        files[file.name] = file
    }

    override fun removeFile(fileName: String) {
        files.remove(fileName)
    }
}

fun main() {
    val scanner = Scanner(System.`in`)
    val docindex = DocumentsApi.create(FileSystemInMemory())

    println("Hi. Type word to search the file name, or 'q' to quit: ")
    println("Available commands:")
    println("    q - quit")
    println("    -a - add new document")
    println("    -r - remove document")
    println("    -ls - list indexed file names")
    println("    -li - list stored indexes")


    while (true) {
        val command = scanner.nextLine().trim()

        if (command == "q") break

        if (command == "-a") {
            print("Enter file name: ")
            val fileName = scanner.nextLine().trim()
            print("Enter file content: ")
            val fileContent = scanner.nextLine().trim()

            when (val result = docindex.register(FileSystemSpi.File(fileName, fileContent))) {
                is DocumentsServiceResult.Success -> println("OK.")
                is DocumentsServiceResult.NoOperation -> println("Document is not indexable")
                is DocumentsServiceResult.Failure -> println("Cannot add document: ${result.message}")
            }

            continue
        }

        if (command == "-r") {
            print("Enter file name: ")
            val fileName = scanner.nextLine().trim()

            when (val result = docindex.remove(fileName)) {
                is DocumentsServiceResult.Success -> println("OK.")
                is DocumentsServiceResult.NoOperation -> println("Document has not been found")
                is DocumentsServiceResult.Failure -> println("Cannot remove document: ${result.message}")
            }

            continue
        }

        if (command == "-ls") {
            println(docindex.listIndexedFileNames())
            continue
        }

        if (command == "-li") {
            println(docindex.listIndexedWords())
            continue
        }

        println(docindex.findFileNames(command))
    }

    println("Goodbye")
}