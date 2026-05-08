package com.airnovel.app.data.api

import com.airnovel.app.data.model.ApiResponse
import com.airnovel.app.data.model.Book
import com.airnovel.app.data.model.Chapter
import com.airnovel.app.data.model.LatestUpdateItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("/api/books")
    suspend fun getBooks(): ApiResponse<List<Book>>

    @GET("/api/books/{book_id}/chapters")
    suspend fun getChapters(
        @Path("book_id") bookId: String
    ): ApiResponse<List<Chapter>>

    @GET("/api/books/{book_id}/chapters/{chapter_id}")
    suspend fun getChapterContent(
        @Path("book_id") bookId: String,
        @Path("chapter_id") chapterId: Int
    ): ApiResponse<Chapter>

    @GET("/api/latest")
    suspend fun getLatestUpdates(
        @Query("limit") limit: Int = 10
    ): ApiResponse<List<LatestUpdateItem>>
}
