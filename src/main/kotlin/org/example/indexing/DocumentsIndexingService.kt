package org.example.org.example.indexing

import arrow.core.Either

internal class DocumentsIndexingService(private val registry: DocumentsRegistrySpi) : DocuemntsIndexingApi {

    override fun register(document: Document): Either<DocumentIndexingProblem, Document> {
        return registry.register(document)
            .mapLeft { it.toDomain() }
    }

    override fun remove(name: DocumentName): Either<DocumentIndexingProblem, Unit> {
        TODO("Not yet implemented")
    }

    override fun query(word: Word): Either<DocumentIndexingProblem, DocumentNamesSet> =
        registry.getAll()
            .map { it.findByWord(word) }
            .mapLeft { it.toDomain() }

}

private fun RegistryProblem.toDomain() =
    DocumentIndexingProblem.RegistrationProblem