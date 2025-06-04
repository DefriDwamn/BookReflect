package com.defri.bookreflect.presentation.profile

import com.defri.bookreflect.core.common.BaseViewModel
import com.defri.bookreflect.core.common.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel<ProfileState, ProfileEvent>() {
    override fun initialState(): ProfileState = ProfileState()

    override fun handleEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.LoadProfile -> loadProfile()
            is ProfileEvent.UpdateProfile -> updateProfile(event.name, event.email)
            is ProfileEvent.Logout -> logout()
        }
    }

    private fun loadProfile() {
        launchWithLoading {
            // TODO: Implement profile loading logic w repository
            setState { 
                copy(
                    profileResult = Result.Success(
                        ProfileData(
                            name = "John Doe",
                            email = "john@example.com",
                            profileImageUrl = null
                        )
                    )
                )
            }
        }
    }

    private fun updateProfile(name: String, email: String) {
        launchWithLoading {
            // TODO: Implement profile update logic w repository
            setState { 
                copy(
                    profileResult = Result.Success(
                        ProfileData(
                            name = name,
                            email = email,
                            profileImageUrl = null
                        )
                    )
                )
            }
        }
    }

    private fun logout() {
        launchWithLoading {
            // TODO: Implement logout logic w repository
            setState { copy(operationResult = Result.Success(Unit)) }
        }
    }
}

data class ProfileState(
    val profileResult: Result<ProfileData> = Result.Success(ProfileData()),
    val operationResult: Result<Unit> = Result.Success(Unit)
)

data class ProfileData(
    val name: String = "",
    val email: String = "",
    val profileImageUrl: String? = null
)

sealed class ProfileEvent {
    object LoadProfile : ProfileEvent()
    data class UpdateProfile(val name: String, val email: String) : ProfileEvent()
    object Logout : ProfileEvent()
} 