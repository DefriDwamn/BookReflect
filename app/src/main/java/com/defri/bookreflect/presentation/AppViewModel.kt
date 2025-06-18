package com.defri.bookreflect.presentation

import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<AppState, AppEvent>() {
    override fun initialState(): AppState = AppState()

    override fun handleEvent(event: AppEvent) {
    }

    val isAuthenticated get() = authRepository.getCurrentUser() != null
}

data class AppState(val dummy: Boolean = false)

sealed class AppEvent {
}