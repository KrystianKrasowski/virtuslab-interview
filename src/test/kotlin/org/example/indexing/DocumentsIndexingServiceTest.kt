package org.example.org.example.indexing

import arrow.core.Either
import arrow.core.right
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class DocumentsRegistryMock(val documents: MutableMap<DocumentName, Document> = mutableMapOf()) : DocumentsRegistrySpi {


    override fun register(document: Document): Either<RegistryProblem, Document> {
        documents[document.name] = document
        return document.right()
    }

    override fun getAll(): Either<RegistryProblem, Documents> {
        return documents.values.toSet().let { Documents(it) }.right()
    }

}

class DocumentsIndexingServiceTest {

    private val registry = DocumentsRegistryMock(mutableMapOf(
        DocumentName("Doc1") to Document(DocumentName("Doc1"), "Brown, fox jumped across th3 fence!"),
        DocumentName("Doc2") to Document(DocumentName("Doc2"), "Lorem ipsum : dolor est"),
        DocumentName("Doc3") to Document(DocumentName("Doc3"), "some example contennt with 1234 digits"),
        DocumentName("Doc4") to Document(DocumentName("Doc4"), "simple content"),
    ))

    private val service get() = DocumentsIndexingService(registry)

    @Test
    fun `should register document by name`() {
        val name = DocumentName("Example-1")
        val document = Document(name, "Example content")
        service.register(document)

        Assertions.assertEquals(registry.documents[name], document)
    }

    @Test
    fun `should remove document by name`() {
    }

    @Test
    fun `shoule retun a set of document names by given word`() {
        // when
        val actual = service.query(Word("fox")).getOrNull()

        // then
        val expected = DocumentNamesSet(setOf(DocumentName("Doc1")))
        Assertions.assertEquals(expected, actual)

    }
}