package org.example.org.example.indexing

import arrow.core.Either

interface DocuemntsIndexingApi {

    fun register(document: Document): Either<DocumentIndexingProblem, Document>

    fun remove(name: DocumentName): Either<DocumentIndexingProblem, Unit>

    fun query(word: Word): Either<DocumentIndexingProblem, DocumentNamesSet>
}

@JvmInline
value class DocumentName(val value: String)

data class Document(val name: DocumentName, val content: String)

sealed interface DocumentIndexingProblem {
    object RegistrationProblem : DocumentIndexingProblem
}

@JvmInline
value class Word(val value: String)

data class DocumentNamesSet(private val documents: Collection<DocumentName>) {
    companion object {
        fun empty(): DocumentNamesSet {
            return DocumentNamesSet(emptySet())
        }
    }
}



