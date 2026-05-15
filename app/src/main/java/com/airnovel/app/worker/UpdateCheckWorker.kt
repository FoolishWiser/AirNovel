package com.airnovel.app.worker

import android.content.Context
import android.util.Log
import androidx.work.*
import com.airnovel.app.data.api.RetrofitClient
import com.airnovel.app.data.local.PreferencesManager
import com.airnovel.app.data.local.ReadStatusManager
import com.airnovel.app.data.repository.NovelRepository
import com.airnovel.app.data.repository.Result as RepoResult
import com.airnovel.app.notification.NotificationHelper
import java.util.concurrent.TimeUnit

class UpdateCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = PreferencesManager(applicationContext)
            val serverUrl = prefs.serverUrl

            if (serverUrl.isBlank()) {
                Log.w(TAG, "服务器地址未设置，跳过更新检查")
                return Result.success()
            }

            RetrofitClient.initialize(serverUrl)
            val repository = NovelRepository()
            val readStatusManager = ReadStatusManager(applicationContext)
            val notificationHelper = NotificationHelper(applicationContext)

            val repoResult = repository.getLatestUpdates()
            return when (repoResult) {
                is RepoResult.Success -> {
                    val updates = repoResult.data
                    if (updates.isNotEmpty()) {
                        val lastKnownChapterId = readStatusManager.getLastKnownChapterId()

                        for (item in updates) {
                            val lastKnown = lastKnownChapterId?.toIntOrNull()
                            if (lastKnown == null || item.chapterId > lastKnown) {
                                notificationHelper.showUpdateNotification(
                                    bookTitle = item.bookTitle,
                                    chapterTitle = item.chapterTitle,
                                    bookId = item.bookId,
                                    chapterId = item.chapterId.toString()
                                )
                            }
                        }

                        val latestChapterId = updates.maxByOrNull { it.chapterId }?.chapterId
                        if (latestChapterId != null) {
                            readStatusManager.saveLastKnownChapterId(latestChapterId.toString())
                        }
                    }

                    prefs.lastCheckedTimestamp = System.currentTimeMillis()
                    Result.success()
                }
                is RepoResult.Error -> {
                    Log.w(TAG, "更新检查失败: ${repoResult.message}")
                    Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "更新检查异常", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "UpdateCheckWorker"
        private const val WORK_NAME = "chapter_update_check"

        fun schedule(context: Context, intervalMinutes: Long = 60) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val request = PeriodicWorkRequestBuilder<UpdateCheckWorker>(
                intervalMinutes, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setBackoffCriteria(
                    BackoffPolicy.EXPONENTIAL,
                    1, TimeUnit.MINUTES
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )

            Log.d(TAG, "更新检查已调度，间隔: ${intervalMinutes}分钟")
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
