package com.airnovel.app.data.update

data class UpdateInfo(
    val latestVersion: String,
    val downloadUrl: String,
    val releaseNotes: String,
    val publishedAt: String
)
