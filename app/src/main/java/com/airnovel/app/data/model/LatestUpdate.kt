package com.airnovel.app.data.model

import com.google.gson.annotations.SerializedName

data class LatestUpdateItem(
    @SerializedName("book_id")
    val bookId: String = "",

    @SerializedName("book_title")
    val bookTitle: String = "",

    @SerializedName("chapter_id")
    val chapterId: Int = 0,

    @SerializedName("chapter_title")
    val chapterTitle: String = "",

    @SerializedName("preview")
    val preview: String = "",

    @SerializedName("updated_at")
    val updatedAt: String = ""
)
