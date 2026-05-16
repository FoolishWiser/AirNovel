package com.airnovel.app.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.airnovel.app.data.api.RetrofitClient
import com.airnovel.app.data.local.PreferencesManager
import com.airnovel.app.data.update.UpdateChecker
import com.airnovel.app.data.update.UpdateInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SettingsUiState(
    val serverUrl: String = "",
    val isTesting: Boolean = false,
    val testResult: String? = null,
    val testSuccess: Boolean = false,
    val checkIntervalMinutes: Long = 60,
    val followSystemTheme: Boolean = true,
    val isDarkMode: Boolean = false,
    val readerFontSize: Float = 18f,
    val readerLineSpacing: Float = 1.6f,
    val readerUseSerif: Boolean = true,
    val isCheckingUpdate: Boolean = false,
    val updateInfo: UpdateInfo? = null,
    val updateError: String? = null,
    val updateMirrorIndex: Int = 0
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.value = SettingsUiState(
            serverUrl = prefs.serverUrl,
            checkIntervalMinutes = prefs.checkIntervalMinutes,
            followSystemTheme = prefs.followSystemTheme,
            isDarkMode = prefs.isDarkMode,
            readerFontSize = prefs.readerFontSize,
            readerLineSpacing = prefs.readerLineSpacing,
            readerUseSerif = prefs.readerUseSerif,
            updateMirrorIndex = prefs.updateMirrorIndex
        )
    }

    fun updateServerUrl(url: String) {
        _uiState.value = _uiState.value.copy(serverUrl = url, testResult = null)
    }

    fun testConnection() {
        val url = _uiState.value.serverUrl
        if (url.isBlank()) {
            _uiState.value = _uiState.value.copy(
                testResult = "请输入服务器地址",
                testSuccess = false
            )
            return
        }
        _uiState.value = _uiState.value.copy(isTesting = true, testResult = null)

        viewModelScope.launch {
            RetrofitClient.testConnection(url) { success, message ->
                _uiState.value = _uiState.value.copy(
                    isTesting = false,
                    testResult = message,
                    testSuccess = success
                )
            }
        }
    }

    fun checkUpdate() {
        _uiState.value = _uiState.value.copy(isCheckingUpdate = true, updateInfo = null, updateError = null)
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                UpdateChecker.checkUpdate()
            }
            result.fold(
                onSuccess = { info ->
                    _uiState.value = _uiState.value.copy(
                        isCheckingUpdate = false,
                        updateInfo = info,
                        updateError = null
                    )
                },
                onFailure = { e ->
                    _uiState.value = _uiState.value.copy(
                        isCheckingUpdate = false,
                        updateInfo = null,
                        updateError = e.localizedMessage ?: "检查更新失败"
                    )
                }
            )
        }
    }

    fun setUpdateMirror(index: Int) {
        _uiState.value = _uiState.value.copy(updateMirrorIndex = index)
        prefs.updateMirrorIndex = index
    }

    fun getDownloadUrl(): String? {
        val info = _uiState.value.updateInfo ?: return null
        return UpdateChecker.getDownloadUrlWithMirror(info.downloadUrl, _uiState.value.updateMirrorIndex)
    }

    fun saveSettings() {
        val state = _uiState.value
        prefs.serverUrl = state.serverUrl
        prefs.checkIntervalMinutes = state.checkIntervalMinutes
        prefs.followSystemTheme = state.followSystemTheme
        prefs.isDarkMode = state.isDarkMode
        prefs.readerFontSize = state.readerFontSize
        prefs.readerLineSpacing = state.readerLineSpacing
        prefs.readerUseSerif = state.readerUseSerif

        if (state.serverUrl.isNotBlank()) {
            RetrofitClient.initialize(state.serverUrl)
        }
    }

    fun setCheckInterval(minutes: Long) {
        _uiState.value = _uiState.value.copy(checkIntervalMinutes = minutes)
        prefs.checkIntervalMinutes = minutes
    }

    fun setFollowSystemTheme(follow: Boolean) {
        _uiState.value = _uiState.value.copy(followSystemTheme = follow)
        prefs.followSystemTheme = follow
    }

    fun setDarkMode(dark: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkMode = dark)
        prefs.isDarkMode = dark
    }

    fun setFontSize(size: Float) {
        _uiState.value = _uiState.value.copy(readerFontSize = size)
        prefs.readerFontSize = size
    }

    fun setLineSpacing(spacing: Float) {
        _uiState.value = _uiState.value.copy(readerLineSpacing = spacing)
        prefs.readerLineSpacing = spacing
    }

    fun setUseSerif(use: Boolean) {
        _uiState.value = _uiState.value.copy(readerUseSerif = use)
        prefs.readerUseSerif = use
    }
}
