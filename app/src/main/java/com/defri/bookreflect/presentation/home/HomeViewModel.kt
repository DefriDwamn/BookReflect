package com.defri.bookreflect.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    // TODO: Add methods to get and update progress, MAYBE recent books, bla-bla...
}

data class HomeUiState(
    val readingProgress: Float = 0f,
    val currentBook: String = "",
    val recentBooks: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
) 