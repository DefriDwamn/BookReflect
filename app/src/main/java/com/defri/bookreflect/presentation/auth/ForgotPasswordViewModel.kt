package com.defri.bookreflect.presentation.auth

import com.defri.bookreflect.core.common.BaseViewModel
import com.defri.bookreflect.core.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor() : BaseViewModel<ForgotPasswordState, ForgotPasswordEvent>() {
    override fun initialState(): ForgotPasswordState = ForgotPasswordState()

    override fun handleEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.SendResetLink -> sendResetLink(event.email)
        }
    }

    private fun sendResetLink(email: String) {
        launchWithLoading {
            if (validateEmail(email)) {
                // TODO: Implement password reset logic w repository
                setState { copy(result = Result.Success(Unit)) }
            } else {
                setState { copy(result = Result.Error(Exception("Please enter a valid email address"))) }
            }
        }
    }

    private fun validateEmail(email: String): Boolean {
        return email.isNotBlank() && email.contains("@") && email.contains(".")
    }
}

data class ForgotPasswordState(
    val result: Result<Unit> = Result.Success(Unit)
)

sealed class ForgotPasswordEvent {
    data class SendResetLink(val email: String) : ForgotPasswordEvent()
} 