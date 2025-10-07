package org.example.indexing

import assertk.assertFailure
import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class DocumentsServiceTest {

    private val stubbedFileMap = mutableMapOf(
        "doc-1.txt" to TextFileSystem.File("doc-1.txt", "Quick brown fox jumps over the lazy dog"),
        "doc-2.txt" to TextFileSystem.File("doc-2.txt", "Lorem ipsum dolor est sit amet"),
        "doc-3.txt" to TextFileSystem.File("doc-3.txt", "My cat is very quick")
    )

    private var fileSystem = TextFileSystemInMemory(stubbedFileMap)

    private val documentsService: DocumentsService by lazy { DocumentsService(fileSystem) }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "quick",
            "quick   ",
            "   quick",
            "   quick   ",
        ]
    )
    fun `should return document file names by word`(fileName: String) {
        // when
        val files = documentsService.findFileNames(fileName)

        // then
        assertThat(files)
            .containsExactlyInAnyOrder("doc-1.txt", "doc-3.txt")
    }

    @Test
    fun `should register new document`() {
        // when
        val result = documentsService.register(
            TextFileSystem.File(
                "doc-4.txt",
                "Brand new file with some quick animal story"
            )
        )

        // then
        assertThat(result)
            .isSuccess()
    }

    @Test
    fun `should index document after registration`() {
        // when
        documentsService.register(TextFileSystem.File("doc-4.txt", "Brand new file with some quick animal story"))
        val files = documentsService.findFileNames("quick")

        // then
        assertThat(files)
            .contains("doc-4.txt")
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "doc-4.txt,''",
            "doc-4.txt,'      '",
            "'', Brand new file with some quick animal story",
            "'     ', Brand new file with some quick animal story",
        ],
        emptyValue = ""
    )
    fun `should not register non-indexable document`(fileName: String, fileContent: String) {
        // given
        val expectedFiles = documentsService.listIndexedFileNames()

        // when
        val result = documentsService.register(TextFileSystem.File(fileName, fileContent))

        // when
        assertThat(result)
            .isNoOperation()

        assertThat(documentsService.listIndexedFileNames())
            .isEqualTo(expectedFiles)
    }

    @Test
    fun `should not index document on adding file failure`() {
        // given
        fileSystem = TextFileSystemInMemory(stubbedFileMap, failingOnAddFile = true)

        // when
        val result = documentsService.register(
            TextFileSystem.File("doc-5.txt", "Clever goat, not quick at all, ate my lunch")
        )

        // then
        assertThat(result)
            .isFailure()

        assertThat(documentsService.listIndexedFileNames())
            .doesNotContain("doc-5.txt")
    }

    @Test
    fun `should remove document by file name`() {
        // when
        val result = documentsService.remove("doc-2.txt")

        // then
        assertThat(result)
            .isSuccess()

        assertThat(documentsService.listIndexedFileNames())
            .doesNotContain("doc-2.txt")
    }

    @Test
    fun `should unindex document after file remove`() {
        // when
        documentsService.remove("doc-2.txt")
        val files = documentsService.findFileNames("ipsum")

        // then
        assertThat(files)
            .doesNotContain("doc-2.txt")
    }

    @Test
    fun `should not unindex document on removing file failure`() {
        // given
        fileSystem = TextFileSystemInMemory(stubbedFileMap, failingOnRemoveFile = true)

        // when
        val result = documentsService.remove("doc-3.txt")

        // then
        assertThat(result)
            .isFailure()

        assertThat(documentsService.listIndexedFileNames())
            .contains("doc-3.txt")
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
        val result = documentsService.remove("")

        // then
        assertThat(result)
            .isNoOperation()

        assertThat(documentsService.listIndexedFileNames())
            .isEqualTo(expectedFiles)

        assertThat(documentsService.countIndexedWords())
            .isEqualTo(expectedWordsNumber)
    }

    @Test
    fun `should index diacritic characters`() {
        // given
        fileSystem = TextFileSystemInMemory(
            mutableMapOf(
                "doc-pl-1.txt" to TextFileSystem.File("doc-pl-1.txt", "zażółć gęślą jaźń")
            )
        )

        // when
        val files1 = documentsService.findFileNames("zażółć")
        val files2 = documentsService.findFileNames("gęślą")
        val files3 = documentsService.findFileNames("jaźń")

        // then
        assertThat(files1).contains("doc-pl-1.txt")
        assertThat(files2).contains("doc-pl-1.txt")
        assertThat(files3).contains("doc-pl-1.txt")
    }

    @Test
    fun `should deduplicate words and indexed documents`() {
        // given
        fileSystem = TextFileSystemInMemory(
            mutableMapOf(
                "doc-1.txt" to TextFileSystem.File("doc-1.txt", "one two one one two three four four"),
                "doc-2.txt" to TextFileSystem.File("doc-2.txt", "two four nine nine one one three"),
                "doc-3.txt" to TextFileSystem.File("doc-3.txt", "five six one two one seven eight seven"),
            )
        )

        // when
        val files = documentsService.listIndexedFileNames()
        val words = documentsService.listIndexedWords()

        // then
        assertThat(files).containsExactlyInAnyOrder("doc-1.txt", "doc-2.txt", "doc-3.txt")
        assertThat(words).containsExactlyInAnyOrder(
            "one",
            "two",
            "three",
            "four",
            "five",
            "six",
            "seven",
            "eight",
            "nine"
        )
    }
}