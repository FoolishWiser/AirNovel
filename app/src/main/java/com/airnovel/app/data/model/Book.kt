package com.airnovel.app.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("code")
    val code: Int = 0,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("msg")
    val message: String = ""
)

data class Book(
    @SerializedName("book_id")
    val bookId: String = "",

    @SerializedName("title")
    val title: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("tags")
    val tags: List<String> = emptyList(),

    @SerializedName("chapter_count")
    val chapterCount: Int = 0,

    @SerializedName("created_at")
    val createdAt: String = "",

    @SerializedName("updated_at")
    val updatedAt: String = "",

    @SerializedName("activated")
    val activated: Boolean = true
)
