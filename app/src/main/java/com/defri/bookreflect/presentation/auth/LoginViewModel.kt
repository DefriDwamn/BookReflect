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
class LoginViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Initial)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            // TODO: Implement login logic w repository
            if (validateInput(email, password)) {
                _uiState.value = LoginUiState.Success
            } else {
                _uiState.value = LoginUiState.Error("Invalid email or password")
            }
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        return email.isNotBlank() && 
               email.contains("@") && 
               password.isNotBlank() && 
               password.length >= 6
    }
}

sealed class LoginUiState {
    object Initial : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
} 