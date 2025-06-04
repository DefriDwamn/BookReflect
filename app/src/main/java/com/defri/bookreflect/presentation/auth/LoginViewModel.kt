package com.defri.bookreflect.presentation.auth

import com.defri.bookreflect.core.common.BaseViewModel
import com.defri.bookreflect.core.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel<LoginState, LoginEvent>() {
    override fun initialState(): LoginState = LoginState()

    override fun handleEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.Login -> login(event.email, event.password)
        }
    }

    private fun login(email: String, password: String) {
        launchWithLoading {
            if (validateInput(email, password)) {
                // TODO: Implement login logic w repository
                setState { copy(result = Result.Success(Unit)) }
            } else {
                setState { copy(result = Result.Error(Exception("Invalid email or password"))) }
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

data class LoginState(
    val result: Result<Unit> = Result.Success(Unit)
)

sealed class LoginEvent {
    data class Login(val email: String, val password: String) : LoginEvent()
} 