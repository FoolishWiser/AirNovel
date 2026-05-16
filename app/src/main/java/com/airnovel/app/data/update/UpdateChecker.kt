package com.airnovel.app.data.update

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class MirrorOption(val label: String, val urlPrefix: String)

object UpdateChecker {

    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val gson = Gson()

    private const val GITHUB_API =
        "https://api.github.com/repos/FoolishWiser/AirNovel/releases/latest"

    val mirrorOptions = listOf(
        MirrorOption("直连", "https://github.com"),
        MirrorOption("ghproxy.net", "https://ghproxy.net/https://github.com"),
        MirrorOption("ghproxy.cn", "https://ghproxy.com/https://github.com"),
    )

    fun checkUpdate(): Result<UpdateInfo> {
        return try {
            val request = Request.Builder()
                .url(GITHUB_API)
                .header("Accept", "application/vnd.github.v3+json")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return Result.failure(Exception("空响应"))

            if (!response.isSuccessful) {
                return Result.failure(Exception("GitHub API 返回 ${response.code}"))
            }

            val release = gson.fromJson(body, GitHubRelease::class.java)
            val tagName = release.tagName.removePrefix("v")
            val downloadUrl = release.assets
                ?.firstOrNull { it.name.endsWith(".apk") }
                ?.browserDownloadUrl
                ?: release.htmlUrl

            Result.success(
                UpdateInfo(
                    latestVersion = tagName,
                    downloadUrl = downloadUrl,
                    releaseNotes = release.body ?: "",
                    publishedAt = release.publishedAt ?: ""
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getDownloadUrlWithMirror(originalUrl: String, mirrorIndex: Int): String {
        if (mirrorIndex <= 0) return originalUrl
        val prefix = mirrorOptions.getOrNull(mirrorIndex)?.urlPrefix ?: return originalUrl
        return originalUrl.replace("https://github.com", prefix)
    }

    private data class GitHubRelease(
        @SerializedName("tag_name") val tagName: String = "",
        @SerializedName("body") val body: String? = null,
        @SerializedName("published_at") val publishedAt: String? = null,
        @SerializedName("html_url") val htmlUrl: String = "",
        @SerializedName("assets") val assets: List<GitHubAsset>? = null
    )

    private data class GitHubAsset(
        @SerializedName("name") val name: String = "",
        @SerializedName("browser_download_url") val browserDownloadUrl: String = ""
    )
}
