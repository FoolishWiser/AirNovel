package com.airnovel.app

import android.app.Application
import com.airnovel.app.data.local.PreferencesManager
import com.airnovel.app.notification.NotificationHelper
import com.airnovel.app.worker.UpdateCheckWorker

class AirNovelApp : Application() {

    lateinit var preferencesManager: PreferencesManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferencesManager = PreferencesManager(this)

        // Initialize notification channel
        NotificationHelper(this)

        // Schedule background update check if server URL is set
        val serverUrl = preferencesManager.serverUrl
        if (serverUrl.isNotBlank()) {
            UpdateCheckWorker.schedule(this, preferencesManager.checkIntervalMinutes)
        }
    }

    companion object {
        lateinit var instance: AirNovelApp
            private set
    }
}
