package com.defri.bookreflect.presentation.auth

import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthState, LoginEvent>() {

    override fun initialState(): AuthState = AuthState()

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(event.email, event.password)
        }
    }

    private fun login(email: String, password: String) {
        launchWithLoading(
            onStart = { copy(isLoading = true, error = null) },
            onError = { copy(error = it.message, isLoading = false) },
            onComplete = { copy(isLoading = false) }
        ) {
            if (!validateInput(email, password)) {
                setState { copy(error = "Invalid input") }
                return@launchWithLoading
            }

            when (val result = authRepository.login(email, password)) {
                is Result.Success -> setState { copy(isAuthenticated = true) }
                is Result.Error -> setState { copy(error = result.exception.message) }
                else -> {}
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

sealed class LoginEvent {
    data class Login(val email: String, val password: String) : LoginEvent()
} 