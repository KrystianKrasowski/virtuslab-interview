package org.example.org.example.indexing

import arrow.core.Either
import arrow.core.right

interface DocumentsRegistrySpi {

    fun register(document: Document): Either<RegistryProblem, Document>
    fun getAll(): Either<RegistryProblem, Documents>
}

class DocumentsRegistryInMemoty : DocumentsRegistrySpi {
    override fun register(document: Document): Either<RegistryProblem, Document> {
        TODO("Not yet implemented")
    }

    override fun getAll(): Either<RegistryProblem, Documents> {
        TODO("Not yet implemented")
    }
}

class DocumentRegistryCachingProxy(private val registry: DocumentsRegistrySpi) : DocumentsRegistrySpi {

    private var cachedDocuments: Documents = Documents.empty()

    override fun register(document: Document): Either<RegistryProblem, Document> =
        registry
            .register(document)
            .onRight { cachedDocuments = cachedDocuments.add(document) }

    override fun getAll(): Either<RegistryProblem, Documents> {
        return cachedDocuments
            .takeIf { it.isNotEmpty() }
            ?.right()
            ?: registry
                .getAll()
                .onRight { cachedDocuments = it }
    }


}

sealed interface RegistryProblem {}