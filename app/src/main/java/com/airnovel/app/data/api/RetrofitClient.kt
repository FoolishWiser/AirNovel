package com.airnovel.app.data.api

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var baseUrl: String = ""
    private var retrofit: Retrofit? = null
    private var apiService: ApiService? = null

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    fun initialize(url: String) {
        val normalizedUrl = normalizeUrl(url)
        if (normalizedUrl == baseUrl && retrofit != null) return
        baseUrl = normalizedUrl
        retrofit = Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit?.create(ApiService::class.java)
    }

    fun getApiService(): ApiService {
        return apiService ?: throw IllegalStateException("RetrofitClient not initialized. Call initialize() first.")
    }

    fun getBaseUrl(): String = baseUrl

    private fun normalizeUrl(url: String): String {
        var normalized = url.trim()
        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            normalized = "http://$normalized"
        }
        if (!normalized.endsWith("/")) {
            normalized = "$normalized/"
        }
        return normalized
    }

    fun isInitialized(): Boolean = apiService != null

    fun testConnection(url: String, callback: (Boolean, String) -> Unit) {
        val normalizedUrl = normalizeUrl(url)
        val tempRetrofit = Retrofit.Builder()
            .baseUrl(normalizedUrl)
            .client(OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val tempService = tempRetrofit.create(ApiService::class.java)

        Thread {
            try {
                val response = runBlocking { tempService.getBooks() }
                if (response.code == 0 && response.data != null) {
                    callback(true, "连接成功！检测到 ${response.data.size} 本书。")
                } else {
                    callback(false, "服务器返回异常：${response.message}")
                }
            } catch (e: Exception) {
                callback(false, "连接失败：${e.localizedMessage ?: "未知错误"}")
            }
        }.start()
    }
}
