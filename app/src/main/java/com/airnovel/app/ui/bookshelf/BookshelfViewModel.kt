package com.airnovel.app.ui.bookshelf

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.airnovel.app.data.local.PreferencesManager
import com.airnovel.app.data.model.Book
import com.airnovel.app.data.repository.NovelRepository
import com.airnovel.app.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class BookshelfUiState(
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val hasServerUrl: Boolean = false
)

class BookshelfViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NovelRepository()
    private val prefs = PreferencesManager(application)

    private val _uiState = MutableStateFlow(BookshelfUiState())
    val uiState: StateFlow<BookshelfUiState> = _uiState.asStateFlow()

    init {
        checkServerUrl()
    }

    private fun checkServerUrl() {
        val url = prefs.serverUrl
        if (url.isNotBlank()) {
            _uiState.value = _uiState.value.copy(hasServerUrl = true)
            com.airnovel.app.data.api.RetrofitClient.initialize(url)
            loadBooks()
        } else {
            _uiState.value = _uiState.value.copy(hasServerUrl = false)
        }
    }

    fun loadBooks() {
        if (!_uiState.value.hasServerUrl) return
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            when (val result = repository.getBooks()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        books = result.data,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, error = null)
        viewModelScope.launch {
            when (val result = repository.getBooks()) {
                is Result.Success -> {
                    _uiState.value = _uiState.value.copy(
                        books = result.data,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
                is Result.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isRefreshing = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
