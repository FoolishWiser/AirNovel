package com.airnovel.app.data.model

import com.google.gson.annotations.SerializedName

data class Chapter(
    @SerializedName("id")
    val id: Int = 0,

    @SerializedName("title")
    val title: String = "",

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("filename")
    val filename: String = "",

    @SerializedName("updated_at")
    val updatedAt: String = ""
)
