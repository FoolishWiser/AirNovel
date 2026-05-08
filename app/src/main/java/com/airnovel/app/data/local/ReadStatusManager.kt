package com.airnovel.app.data.local

import android.content.Context
import android.content.SharedPreferences

class ReadStatusManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_READ_STATUS, Context.MODE_PRIVATE)

    fun markAsRead(bookId: String, chapterId: String) {
        prefs.edit().putBoolean(getKey(bookId, chapterId), true).apply()
    }

    fun isRead(bookId: String, chapterId: String): Boolean {
        return prefs.getBoolean(getKey(bookId, chapterId), false)
    }

    fun markAllAsRead(bookId: String, chapterIds: List<String>) {
        prefs.edit().apply {
            chapterIds.forEach { id ->
                putBoolean(getKey(bookId, id), true)
            }
            apply()
        }
    }

    fun getLatestReadChapterId(bookId: String): String? {
        val all = prefs.all
        val prefix = "${bookId}_"
        return all.filterKeys { it.startsWith(prefix) && it.endsWith("_read") }
            .maxByOrNull { it.value.toString() }?.key
            ?.removeSuffix("_read")
            ?.removePrefix(prefix)
    }

    fun getLatestReadChapterIds(): Map<String, String> {
        val all = prefs.all
        val result = mutableMapOf<String, String>()
        all.forEach { (key, value) ->
            val parts = key.split("_")
            if (parts.size >= 3 && value == true) {
                val bookId = parts[0]
                val chapterId = parts[1]
                val current = result[bookId]
                if (current == null || chapterId > current) {
                    result[bookId] = chapterId
                }
            }
        }
        return result
    }

    fun saveLastKnownChapterId(chapterId: String) {
        prefs.edit().putString(KEY_LAST_KNOWN_CHAPTER, chapterId).apply()
    }

    fun getLastKnownChapterId(): String? {
        return prefs.getString(KEY_LAST_KNOWN_CHAPTER, null)
    }

    fun clearBookReadStatus(bookId: String) {
        val prefix = "${bookId}_"
        prefs.edit().apply {
            prefs.all.filterKeys { it.startsWith(prefix) }.forEach { (key, _) ->
                remove(key)
            }
            apply()
        }
    }

    private fun getKey(bookId: String, chapterId: String): String =
        "${bookId}_${chapterId}_read"

    companion object {
        private const val PREFS_READ_STATUS = "airnovel_read_status"
        private const val KEY_LAST_KNOWN_CHAPTER = "last_known_chapter_id"
    }
}
