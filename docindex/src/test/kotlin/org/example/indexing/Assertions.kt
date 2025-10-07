package org.example.indexing

import assertk.Assert
import assertk.assertions.support.expected

fun Assert<DocumentsServiceResult>.isSuccess(): Assert<DocumentsServiceResult> = transform { actual ->
    actual as? DocumentsServiceResult.Success ?: expected("result to be success")
}

fun Assert<DocumentsServiceResult>.isNoOperation(): Assert<DocumentsServiceResult> = transform { actual ->
    actual as? DocumentsServiceResult.NoOperation ?: expected("result to be no-op")
}

fun Assert<DocumentsServiceResult>.isFailure(): Assert<DocumentsServiceResult> = transform { actual ->
    actual as? DocumentsServiceResult.Failure ?: expected("result to be failure")
}
