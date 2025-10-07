package org.example.indexing

sealed interface DocumentsServiceResult {
    object Success : DocumentsServiceResult
    object NoOperation : DocumentsServiceResult
    data class Failure(val message: String) : DocumentsServiceResult
}