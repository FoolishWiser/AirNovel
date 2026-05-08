package com.airnovel.app.ui.reader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.airnovel.app.data.local.PreferencesManager
import com.airnovel.app.data.model.Chapter
import com.airnovel.app.data.repository.NovelRepository
import com.airnovel.app.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ReaderUiState(
    val bookTitle: String = "",
    val chapterTitle: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isNightMode: Boolean = false,
    val fontSize: Float = 18f,
    val lineSpacing: Float = 1.6f,
    val useSerif: Boolean = true,
    val currentChapterIndex: Int = 0,
    val totalChapters: Int = 0,
    val hasNextChapter: Boolean = false,
    val hasPrevChapter: Boolean = false,
    val scrollProgress: Float = 0f
)

class ReaderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NovelRepository()
    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState.asStateFlow()

    private var bookId: String = ""
    private var chapterIdList: List<String> = emptyList()

    fun initialize(
        bookId: String,
        chapterId: String,
        bookTitle: String,
        chapterIndex: Int,
        chapterIds: List<String>
    ) {
        this.bookId = bookId
        this.chapterIdList = chapterIds

        _uiState.value = _uiState.value.copy(
            bookTitle = bookTitle,
            isNightMode = prefs.readerNightMode,
            fontSize = prefs.readerFontSize,
            lineSpacing = prefs.readerLineSpacing,
            useSerif = prefs.readerUseSerif,
            currentChapterIndex = chapterIndex,
            totalChapters = chapterIds.size,
            hasNextChapter = chapterIndex < chapterIds.size - 1,
            hasPrevChapter = chapterIndex > 0
        )

        loadChapter(chapterId)
    }

    fun loadChapter(chapterId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, scrollProgress = 0f)
        viewModelScope.launch {
            when (val result = repository.getChapterContent(bookId, chapterId.toIntOrNull() ?: 0)) {
                is Result.Success -> {
                    val chapter = result.data
                    _uiState.value = _uiState.value.copy(
                        chapterTitle = chapter.title,
                        content = chapter.content ?: "（本章暂无内容）",
                        isLoading = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun goToNextChapter() {
        val currentIndex = _uiState.value.currentChapterIndex
        if (currentIndex < chapterIdList.size - 1) {
            val nextIndex = currentIndex + 1
            _uiState.value = _uiState.value.copy(
                currentChapterIndex = nextIndex,
                hasNextChapter = nextIndex < chapterIdList.size - 1,
                hasPrevChapter = true
            )
            loadChapter(chapterIdList[nextIndex])
        }
    }

    fun goToPrevChapter() {
        val currentIndex = _uiState.value.currentChapterIndex
        if (currentIndex > 0) {
            val prevIndex = currentIndex - 1
            _uiState.value = _uiState.value.copy(
                currentChapterIndex = prevIndex,
                hasNextChapter = true,
                hasPrevChapter = prevIndex > 0
            )
            loadChapter(chapterIdList[prevIndex])
        }
    }

    fun toggleNightMode() {
        val newMode = !_uiState.value.isNightMode
        _uiState.value = _uiState.value.copy(isNightMode = newMode)
        prefs.readerNightMode = newMode
    }

    fun updateScrollProgress(progress: Float) {
        _uiState.value = _uiState.value.copy(scrollProgress = progress.coerceIn(0f, 1f))
    }

    fun updateFontSize(size: Float) {
        _uiState.value = _uiState.value.copy(fontSize = size)
        prefs.readerFontSize = size
    }

    fun updateLineSpacing(spacing: Float) {
        _uiState.value = _uiState.value.copy(lineSpacing = spacing)
        prefs.readerLineSpacing = spacing
    }

    fun toggleSerif() {
        val newVal = !_uiState.value.useSerif
        _uiState.value = _uiState.value.copy(useSerif = newVal)
        prefs.readerUseSerif = newVal
    }
}
