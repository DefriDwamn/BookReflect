package com.defri.bookreflect.presentation.profile

import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : BaseViewModel<ProfileState, ProfileEvent>() {
    private val _event = Channel<UiEvent>()
    val event = _event.receiveAsFlow()

    override fun initialState(): ProfileState = ProfileState()

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.UpdateProfile -> updateProfile(event.name)
            is ProfileEvent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        launchWithLoading(
            onStart = { copy(isLoading = true, error = null) },
            onError = { copy(error = it.message, isLoading = false) },
            onComplete = { copy(isLoading = false) }
        ) {
            val result = authRepository.getUserProfile()
            if (result is Result.Success) {
                setState { copy(profile = result.data) }
            } else if (result is Result.Error) {
                setState { copy(error = result.exception.message) }
            }
        }
    }

    private fun updateProfile(name: String) {
        launchWithLoading(
            onStart = { copy(isLoading = true, error = null) },
            onError = { copy(error = it.message, isLoading = false) },
            onComplete = { copy(isLoading = false) }
        ) {
            val result = authRepository.updateUserName(name)
            if (result is Result.Success) {
                loadProfile()
            } else if (result is Result.Error) {
                setState { copy(error = result.exception.message) }
            }
        }
    }

    private fun logout() {
        launchWithLoading(
            onStart = { copy(isLoading = true, error = null) },
            onError = { copy(error = it.message, isLoading = false) },
            onComplete = { copy(isLoading = false) }
        ) {
            authRepository.logout()
            _event.send(UiEvent.LogoutSuccess)
        }
    }
}

data class ProfileState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val profile: ProfileData? = null
)

data class ProfileData(
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null
)


sealed class ProfileEvent {
    object LoadProfile : ProfileEvent()
    data class UpdateProfile(val name: String) : ProfileEvent()
    object Logout : ProfileEvent()
}

sealed class UiEvent {
    object LogoutSuccess : UiEvent()
}

