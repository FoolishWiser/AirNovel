package com.airnovel.app.data.repository

import com.airnovel.app.data.api.RetrofitClient
import com.airnovel.app.data.model.Book
import com.airnovel.app.data.model.Chapter
import com.airnovel.app.data.model.LatestUpdateItem

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class NovelRepository {

    private val api get() = RetrofitClient.getApiService()

    suspend fun getBooks(): Result<List<Book>> {
        return try {
            val response = api.getBooks()
            if (response.code == 0 && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message.ifEmpty { "获取书籍列表失败" })
            }
        } catch (e: Exception) {
            Result.Error(parseError(e))
        }
    }

    suspend fun getChapters(bookId: String): Result<List<Chapter>> {
        return try {
            val response = api.getChapters(bookId)
            if (response.code == 0 && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message.ifEmpty { "获取章节列表失败" })
            }
        } catch (e: Exception) {
            Result.Error(parseError(e))
        }
    }

    suspend fun getChapterContent(bookId: String, chapterId: Int): Result<Chapter> {
        return try {
            val response = api.getChapterContent(bookId, chapterId)
            if (response.code == 0 && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message.ifEmpty { "获取章节内容失败" })
            }
        } catch (e: Exception) {
            Result.Error(parseError(e))
        }
    }

    suspend fun getLatestUpdates(): Result<List<LatestUpdateItem>> {
        return try {
            val response = api.getLatestUpdates()
            if (response.code == 0 && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message.ifEmpty { "获取更新列表失败" })
            }
        } catch (e: Exception) {
            Result.Error(parseError(e))
        }
    }

    private fun parseError(e: Exception): String {
        return when {
            e is java.net.ConnectException || e is java.net.UnknownHostException ->
                "无法连接到服务器，请检查网络和地址设置"
            e is java.net.SocketTimeoutException ->
                "连接超时，请检查服务端是否运行"
            e is java.io.IOException ->
                "网络错误：${e.localizedMessage ?: "请重试"}"
            else ->
                "未知错误：${e.localizedMessage ?: "请重试"}"
        }
    }
}
