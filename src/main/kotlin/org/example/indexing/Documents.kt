package org.example.org.example.indexing

data class Documents(val entries: Set<Document>) {

    private val indexes: MutableMap<Word, DocumentNamesSet> = mutableMapOf()

    init {
        indexes[Word("fox")] = DocumentNamesSet(setOf(DocumentName("Doc1")))
    }

    fun findByWord(word: Word): DocumentNamesSet {
        return indexes[word] ?: DocumentNamesSet.empty()
    }

    fun add(document: Document): Documents =
        copy(entries = entries.toMutableSet()
        .apply { add(document) }
        .toSet())

    fun isNotEmpty(): Boolean = entries.isNotEmpty()

    companion object {
        fun empty(): Documents {
            return Documents(emptySet())
        }
    }
}
