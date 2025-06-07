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
        launchWithLoading {
            setState { copy(isLoading = true, error = null) }

            if (validateInput(email, password)) {
                when (val result = authRepository.login(email, password)) {
                    is Result.Success -> {
                        setState { copy(isAuthenticated = true, isLoading = false) }
                    }
                    is Result.Error -> {
                        setState { copy(error = result.exception.message, isLoading = false) }
                    }
                    else -> {}
                }
            } else {
                setState { copy(error = "Invalid input", isLoading = false) }
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