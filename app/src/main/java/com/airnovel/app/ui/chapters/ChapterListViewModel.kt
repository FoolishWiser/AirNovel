package com.airnovel.app.ui.chapters

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.airnovel.app.data.local.ChapterCache
import com.airnovel.app.data.local.ReadStatusManager
import com.airnovel.app.data.model.Chapter
import com.airnovel.app.data.repository.NovelRepository
import com.airnovel.app.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChapterListUiState(
    val bookTitle: String = "",
    val chapters: List<Chapter> = emptyList(),
    val readChapterIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ChapterListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NovelRepository()
    private val readStatusManager = ReadStatusManager(application)

    private val _uiState = MutableStateFlow(ChapterListUiState())
    val uiState: StateFlow<ChapterListUiState> = _uiState.asStateFlow()

    private var bookId: String = ""

    fun loadChapters(bookId: String) {
        this.bookId = bookId
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            when (val result = repository.getChapters(bookId)) {
                is Result.Success -> {
                    val chapters = result.data
                    val readChapterIds = mutableSetOf<String>()
                    chapters.forEach { chapter ->
                        if (readStatusManager.isRead(bookId, chapter.id.toString())) {
                            readChapterIds.add(chapter.id.toString())
                        }
                    }
                    ChapterCache.putChapterIds(bookId, chapters.map { it.id.toString() })
                    _uiState.value = _uiState.value.copy(
                        chapters = chapters,
                        readChapterIds = readChapterIds,
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

    fun markAsRead(chapterId: String) {
        readStatusManager.markAsRead(bookId, chapterId)
        val updatedSet = _uiState.value.readChapterIds.toMutableSet()
        updatedSet.add(chapterId)
        _uiState.value = _uiState.value.copy(readChapterIds = updatedSet)
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
