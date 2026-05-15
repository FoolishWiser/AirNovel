package com.airnovel.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.airnovel.app.MainActivity

import java.util.concurrent.atomic.AtomicInteger

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val requestCodeCounter = AtomicInteger(0)

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "小说章节更新通知"
            enableVibration(false)
            setSound(null, null)
            enableLights(false)
        }
        notificationManager.createNotificationChannel(channel)
    }

    fun showUpdateNotification(
        bookTitle: String,
        chapterTitle: String,
        bookId: String,
        chapterId: String
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("bookId", bookId)
            putExtra("chapterId", chapterId)
            putExtra("bookTitle", bookTitle)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            requestCodeCounter.getAndIncrement(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("《$bookTitle》更新了")
            .setContentText(chapterTitle)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            "${bookId}_$chapterId".hashCode(),
            notification
        )
    }

    companion object {
        const val CHANNEL_ID = "novel_update_channel"
        const val CHANNEL_NAME = "小说更新"
    }
}
