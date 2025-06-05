package com.defri.bookreflect.presentation.auth

import com.defri.bookreflect.core.common.BaseViewModel
import com.defri.bookreflect.core.common.Result
import com.defri.bookreflect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AuthState, RegisterEvent>() {

    override fun initialState(): AuthState = AuthState()

    override fun handleEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.Register -> register(
                event.name,
                event.email,
                event.password,
                event.confirmPassword
            )
        }
    }

    private fun register(name: String, email: String, password: String, confirmPassword: String) {
        launchWithLoading {
            setState { copy(isLoading = true, error = null) }

            when {
                !validateName(name) -> {
                    setState { copy(error = "Invalid name", isLoading = false) }
                }
                !validateEmail(email) -> {
                    setState { copy(error = "Invalid email", isLoading = false) }
                }
                !validatePassword(password) -> {
                    setState { copy(error = "Password too short", isLoading = false) }
                }
                password != confirmPassword -> {
                    setState { copy(error = "Passwords don't match", isLoading = false) }
                }
                else -> {
                    when (val result = authRepository.register(name, email, password)) {
                        is Result.Success -> {
                            setState { copy(isAuthenticated = true, isLoading = false) }
                        }
                        is Result.Error -> {
                            setState { copy(error = result.exception.message, isLoading = false) }
                        }
                        else -> {}
                    }
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

sealed class RegisterEvent {
    data class Register(
        val name: String,
        val email: String,
        val password: String,
        val confirmPassword: String
    ) : RegisterEvent()
} 