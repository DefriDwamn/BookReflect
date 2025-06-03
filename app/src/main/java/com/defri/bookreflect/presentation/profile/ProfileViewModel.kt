package com.defri.bookreflect.presentation.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    // TODO: Add methods to get and update profile data
}

data class ProfileUiState(
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) 