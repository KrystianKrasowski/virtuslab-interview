package org.example.indexing

internal data class DocumentIndex(private val index: Map<String, Set<String>>) {

   fun find(word: String) =
       index[word] ?: emptySet()

   fun append(delta: DocumentIndex): DocumentIndex =
       delta.takeUnless { it.isEmpty() }
           ?.let { (index.keys + delta.index.keys) }
           ?.associateWith { addFileNamesAt(it, delta) }
           ?.let { DocumentIndex(it) }
           ?: this

   fun remove(delta: DocumentIndex): DocumentIndex =
       delta.takeUnless { it.isEmpty() }
           ?.let { (index.keys + delta.index.keys) }
           ?.associateWith { subtractFileNamesAt(it, delta) }
           ?.filterValues { it.isNotEmpty() }
           ?.let { DocumentIndex(it) }
           ?: this

   fun isEmpty(): Boolean =
       index.isEmpty()

   private fun addFileNamesAt(word: String, other: DocumentIndex) =
       index[word].orEmpty() + other.index[word].orEmpty()

   private fun subtractFileNamesAt(word: String, other: DocumentIndex) =
       index[word].orEmpty() - other.index[word].orEmpty()
}