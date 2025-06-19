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
        when (event) {
            is AppEvent.SetSelectedBookForMood -> {
                setState {
                    copy(
                        selectedBookIdForMood = event.bookId,
                        selectedBookTitleForMood = event.bookTitle
                    )
                }
            }
            AppEvent.ClearSelectedBookForMood -> {
                setState {
                    copy(
                        selectedBookIdForMood = null,
                        selectedBookTitleForMood = null
                    )
                }
            }
        }
    }

    val isAuthenticated get() = authRepository.getCurrentUser() != null
}

data class AppState(
    val selectedBookIdForMood: String? = null,
    val selectedBookTitleForMood: String? = null
)

sealed class AppEvent {
    data class SetSelectedBookForMood(val bookId: String, val bookTitle: String) : AppEvent()
    object ClearSelectedBookForMood : AppEvent()
}