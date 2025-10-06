package org.example.indexing

import assertk.assertThat
import assertk.assertions.containsExactly
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import org.junit.jupiter.api.Test

class DocumentsServiceTest {

    private var fileSystem = TextFileSystemInMemory(
        mutableMapOf(
            "doc-1.txt" to TextFileSystem.File("doc-1.txt", "Quick brown fox jumps over the lazy dog"),
            "doc-2.txt" to TextFileSystem.File("doc-2.txt", "Lorem ipsum dolor est sit amet"),
            "doc-3.txt" to TextFileSystem.File("doc-3.txt", "My cat is very quick")
        )
    )

    private val documentsService: DocumentsService by lazy { DocumentsService(fileSystem) }

    @Test
    fun `should return document file names by word`() {
        // when
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files.toList())
            .containsExactly("doc-1.txt", "doc-3.txt")
    }

    @Test
    fun `should add new document`() {
        // given
        val content = "Brand new file with some quick animal story"

        // when
        documentsService.add(TextFileSystem.File("doc-4.txt", content))

        // then
        assertThat(fileSystem.getFileContent("doc-4.txt"))
            .isEqualTo(content)
    }

    @Test
    fun `should index newly added file`() {
        // when
        documentsService.add(TextFileSystem.File("doc-5.txt", "Clever goat, not quick at all, ate my lunch"))
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files.toList())
            .containsExactly("doc-1.txt", "doc-3.txt", "doc-5.txt")
    }

    @Test
    fun `should remove document by file name`() {
        // when
        documentsService.remove("doc-2.txt")

        // then
        assertThat(fileSystem.getFileContent("doc-2.txt")).isNull()
    }

    @Test
    fun `should remove deleted file from index`() {
        // when
        documentsService.remove("doc-1.txt")
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files.toList())
            .containsExactly("doc-3.txt")
    }

    @Test
    fun `should return no files if word has not been found`() {
        // when
        val files = documentsService.findFileNames("unknown")

        // then
        assertThat(files).isEmpty()
    }
}