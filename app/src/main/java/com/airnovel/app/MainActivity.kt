package com.airnovel.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.airnovel.app.data.local.PreferencesManager
import com.airnovel.app.ui.navigation.AirNovelNavGraph
import com.airnovel.app.ui.navigation.Routes
import com.airnovel.app.ui.theme.AirNovelTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // Permission result handled - notification will work or be silently ignored
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request notification permission for Android 13+
        requestNotificationPermission()

        val prefs = PreferencesManager(this)
        val isFirstLaunch = prefs.serverUrl.isBlank()

        // Check if opened from notification
        val notificationBookId = intent?.getStringExtra("bookId")
        val notificationChapterId = intent?.getStringExtra("chapterId")

        setContent {
            val prefsManager = remember { PreferencesManager(this@MainActivity) }
            val followSystem = prefsManager.followSystemTheme
            val isDarkMode = prefsManager.isDarkMode
            val isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()

            val useDarkTheme = if (followSystem) isSystemDark else isDarkMode

            AirNovelTheme(
                darkTheme = useDarkTheme,
                followSystem = followSystem
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    val startDest = if (isFirstLaunch) {
                        Routes.SETTINGS_FIRST
                    } else {
                        Routes.BOOKSHELF
                    }

                    AirNovelNavGraph(
                        navController = navController,
                        startDestination = startDest,
                        isFirstLaunch = isFirstLaunch
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
