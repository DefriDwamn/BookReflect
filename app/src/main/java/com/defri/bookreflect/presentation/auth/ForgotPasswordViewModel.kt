package com.defri.bookreflect.presentation.auth

import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<ForgotPasswordState, ForgotPasswordEvent>() {
    override fun initialState(): ForgotPasswordState = ForgotPasswordState()

    override fun handleEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.SendResetLink -> sendResetLink(event.email)
        }
    }

    private fun sendResetLink(email: String) {
        launchWithLoading(
            onStart = { copy(isLoading = true, error = null) },
            onError = { copy(error = it.message, isLoading = false) },
            onComplete = { copy(isLoading = false) }
        ) {
            if (!validateEmail(email)) {
                setState { copy(error = "Please enter a valid email") }
                return@launchWithLoading
            }

            when (val result = authRepository.sendPasswordResetEmail(email)) {
                is Result.Success -> setState { copy(isResetSent = true) }
                is Result.Error -> setState { copy(error = result.exception.message) }
                else -> {}
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && email.contains("@") && email.contains(".")
    }
}

data class ForgotPasswordState(
    val isResetSent: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class ForgotPasswordEvent {
    data class SendResetLink(val email: String) : ForgotPasswordEvent()
}