package com.airnovel.app.data.local

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var serverUrl: String
        get() = prefs.getString(KEY_SERVER_URL, "") ?: ""
        set(value) = prefs.edit().putString(KEY_SERVER_URL, value).apply()

    var lastCheckedTimestamp: Long
        get() = prefs.getLong(KEY_LAST_CHECKED, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_CHECKED, value).apply()

    var checkIntervalMinutes: Long
        get() = prefs.getLong(KEY_CHECK_INTERVAL, 60L)
        set(value) = prefs.edit().putLong(KEY_CHECK_INTERVAL, value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_DARK_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_DARK_MODE, value).apply()

    var followSystemTheme: Boolean
        get() = prefs.getBoolean(KEY_FOLLOW_SYSTEM, true)
        set(value) = prefs.edit().putBoolean(KEY_FOLLOW_SYSTEM, value).apply()

    var readerFontSize: Float
        get() = prefs.getFloat(KEY_FONT_SIZE, 18f)
        set(value) = prefs.edit().putFloat(KEY_FONT_SIZE, value).apply()

    var readerLineSpacing: Float
        get() = prefs.getFloat(KEY_LINE_SPACING, 1.6f)
        set(value) = prefs.edit().putFloat(KEY_LINE_SPACING, value).apply()

    var readerUseSerif: Boolean
        get() = prefs.getBoolean(KEY_USE_SERIF, true)
        set(value) = prefs.edit().putBoolean(KEY_USE_SERIF, value).apply()

    var readerNightMode: Boolean
        get() = prefs.getBoolean(KEY_READER_NIGHT, false)
        set(value) = prefs.edit().putBoolean(KEY_READER_NIGHT, value).apply()

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "airnovel_prefs"
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_LAST_CHECKED = "last_checked"
        private const val KEY_CHECK_INTERVAL = "check_interval"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_FOLLOW_SYSTEM = "follow_system"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_LINE_SPACING = "line_spacing"
        private const val KEY_USE_SERIF = "use_serif"
        private const val KEY_READER_NIGHT = "reader_night"
    }
}
