package com.airnovel.app.data.local

object ChapterCache {
    private val cache = mutableMapOf<String, List<String>>()

    fun putChapterIds(bookId: String, chapterIds: List<String>) {
        cache[bookId] = chapterIds
    }

    fun getChapterIds(bookId: String): List<String> {
        return cache[bookId] ?: emptyList()
    }

    fun clear() {
        cache.clear()
    }

    fun remove(bookId: String) {
        cache.remove(bookId)
    }
}
