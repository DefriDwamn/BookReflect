package com.defri.bookreflect.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<ForgotPasswordUiState>(ForgotPasswordUiState.Initial)
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun sendResetLink(email: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading
            if (validateEmail(email)) {
                // TODO: Implement password reset logic w repository
                _uiState.value = ForgotPasswordUiState.Success
            } else {
                _uiState.value = ForgotPasswordUiState.Error("Please enter a valid email address")
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && email.contains("@") && email.contains(".")
    }
}

sealed class ForgotPasswordUiState {
    object Initial : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    object Success : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
} 