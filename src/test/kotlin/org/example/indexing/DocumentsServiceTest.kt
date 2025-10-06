package org.example.indexing

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.doesNotContain
import assertk.assertions.hasMessage
import assertk.assertions.isEmpty
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DocumentsServiceTest {

    private val stubbedFileMap = mutableMapOf(
        "doc-1.txt" to TextFileSystem.File("doc-1.txt", "Quick brown fox jumps over the lazy dog"),
        "doc-2.txt" to TextFileSystem.File("doc-2.txt", "Lorem ipsum dolor est sit amet"),
        "doc-3.txt" to TextFileSystem.File("doc-3.txt", "My cat is very quick")
    )

    private var fileSystem = TextFileSystemInMemory(stubbedFileMap)

    private val documentsService: DocumentsService by lazy { DocumentsService(fileSystem) }

    @Test
    fun `should return document file names by word`() {
        // when
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files)
            .containsExactlyInAnyOrder("doc-1.txt", "doc-3.txt")
    }

    @Test
    fun `should add new document`() {
        // given
        val file = TextFileSystem.File("doc-4.txt", "Brand new file with some quick animal story")

        // when
        documentsService.add(file)

        // then
        assertThat(fileSystem.getFileByName(file.name))
            .isEqualTo(file)
    }

    @Test
    fun `should index new document`() {
        // when
        documentsService.add(TextFileSystem.File("doc-5.txt", "Clever goat, not quick at all, ate my lunch"))
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files)
            .containsExactlyInAnyOrder("doc-1.txt", "doc-3.txt", "doc-5.txt")
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "     "])
    fun `should not add empty file or index it`(content: String) {
        // when
        documentsService.add(TextFileSystem.File("doc-5.txt", content))
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files).containsExactlyInAnyOrder("doc-1.txt", "doc-3.txt")
        assertThat(fileSystem.getFileByName("doc-5.txt")).isNull()
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "    "])
    fun `should not add unnamed file or index it`(fileName: String) {
        // when
        documentsService.add(TextFileSystem.File(fileName, "some quick content"))
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files).containsExactlyInAnyOrder("doc-1.txt", "doc-3.txt")
        assertThat(fileSystem.getFileByName("")).isNull()
    }

    @Test
    fun `should remove document by file name`() {
        // when
        documentsService.remove("doc-2.txt")

        // then
        assertThat(fileSystem.getFileByName("doc-2.txt")).isNull()
    }

    @Test
    fun `should remove deleted file from index`() {
        // when
        documentsService.remove("doc-1.txt")
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files)
            .containsExactlyInAnyOrder("doc-3.txt")
    }

    @Test
    fun `should return no files if word has not been found`() {
        // when
        val files = documentsService.findFileNames("unknown")

        // then
        assertThat(files).isEmpty()
    }

    @Test
    fun `should fail fast when cannot fetch file names from filesystem`() {
        // given
        fileSystem = TextFileSystemInMemory(stubbedFileMap, failingOnGetFileNames = true)

        // when
        // assumed, that this service would instantiate with application startup, invoking lazy init
        assertFailure { documentsService }
    }

    @Test
    fun `should fail fast when cannot fetch file by name from the start`() {
        // given
        fileSystem = TextFileSystemInMemory(stubbedFileMap, failingOnGetFileByName = true)

        // when
        assertFailure { documentsService }
    }

    @Test
    fun `should not index file on add failure`() {
        // given
        fileSystem = TextFileSystemInMemory(stubbedFileMap, failingOnAddFile = true)

        // when
        val result = documentsService.runCatching {
            add(TextFileSystem.File("doc-5.txt", "Clever goat, not quick at all, ate my lunch"))
        }

        // then
        assertThat(result)
            .isFailure()
            .hasMessage("Cannot add file")

        assertThat(documentsService.listIndexedFileNames())
            .doesNotContain("doc-5.txt")
    }

    @Test
    fun `should not unindex file on remove failure`() {
        // given
        fileSystem = TextFileSystemInMemory(stubbedFileMap, failingOnRemoveFile = true)

        // when
        val result = documentsService.runCatching {
            remove("doc-3.txt")
        }

        // then
        assertThat(result)
            .isFailure()
            .hasMessage("Cannot remove file")

        assertThat(documentsService.listIndexedFileNames())
            .contains("doc-3.txt")
    }

    @Test
    fun `should not index text files without words`() {
        // given
        fileSystem = TextFileSystemInMemory(mutableMapOf("doc-9.txt" to TextFileSystem.File("doc-9.txt", "")))

        // then
        assertThat(documentsService.countIndexedWords()).isEqualTo(0)
    }

    @Test
    fun `should do nothing on blank filename removal`() {
        // given
        val expectedFiles = documentsService.listIndexedFileNames()
        val expectedWordsNumber = documentsService.countIndexedWords()

        // when
        documentsService.remove("")

        // then
        assertThat(documentsService.listIndexedFileNames()).isEqualTo(expectedFiles)
        assertThat(documentsService.countIndexedWords()).isEqualTo(expectedWordsNumber)
    }
}