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
class RegisterViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Initial)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading
            
            when {
                !validateName(name) -> {
                    _uiState.value = RegisterUiState.Error("Please enter a valid name")
                }
                !validateEmail(email) -> {
                    _uiState.value = RegisterUiState.Error("Please enter a valid email")
                }
                !validatePassword(password) -> {
                    _uiState.value = RegisterUiState.Error("Password must be at least 6 characters")
                }
                password != confirmPassword -> {
                    _uiState.value = RegisterUiState.Error("Passwords do not match")
                }
                else -> {
                    // TODO: Implement registration logic w repository
                    _uiState.value = RegisterUiState.Success
                }
            }
        }
    }

    private fun validateName(name: String): Boolean {
        return name.isNotBlank() && name.length >= 2
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && email.contains("@") && email.contains(".")
    }

    private fun validatePassword(password: String): Boolean {
        return password.isNotBlank() && password.length >= 6
    }
}

sealed class RegisterUiState {
    object Initial : RegisterUiState()
    object Loading : RegisterUiState()
    object Success : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
} 