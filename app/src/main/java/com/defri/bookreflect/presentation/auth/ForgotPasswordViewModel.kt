package com.defri.bookreflect.presentation.auth

import com.defri.bookreflect.core.common.BaseViewModel
import com.defri.bookreflect.core.common.Result
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
        launchWithLoading {
            setState { copy(isLoading = true, error = null) }

            when {
                !validateEmail(email) -> {
                    setState { copy(error = "Please enter a valid email", isLoading = false) }
                }
                else -> {
                    when (val result = authRepository.sendPasswordResetEmail(email)) {
                        is Result.Success -> {
                            setState { copy(isResetSent = true, isLoading = false) }
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