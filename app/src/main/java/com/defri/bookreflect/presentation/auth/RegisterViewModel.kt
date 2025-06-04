package com.defri.bookreflect.presentation.auth

import com.defri.bookreflect.core.common.BaseViewModel
import com.defri.bookreflect.core.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor() : BaseViewModel<RegisterState, RegisterEvent>() {
    override fun initialState(): RegisterState = RegisterState()

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
            when {
                !validateName(name) -> {
                    setState { copy(result = Result.Error(Exception("Please enter a valid name"))) }
                }
                !validateEmail(email) -> {
                    setState { copy(result = Result.Error(Exception("Please enter a valid email"))) }
                }
                !validatePassword(password) -> {
                    setState { copy(result = Result.Error(Exception("Password must be at least 6 characters"))) }
                }
                password != confirmPassword -> {
                    setState { copy(result = Result.Error(Exception("Passwords do not match"))) }
                }
                else -> {
                    // TODO: Implement registration logic w repository
                    setState { copy(result = Result.Success(Unit)) }
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

data class RegisterState(
    val result: Result<Unit> = Result.Success(Unit)
)

sealed class RegisterEvent {
    data class Register(
        val name: String,
        val email: String,
        val password: String,
        val confirmPassword: String
    ) : RegisterEvent()
} 