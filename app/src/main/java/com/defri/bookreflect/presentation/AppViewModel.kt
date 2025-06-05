package com.defri.bookreflect.presentation

import androidx.lifecycle.ViewModel
import com.defri.bookreflect.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val isAuthenticated get() = authRepository.getCurrentUser() != null
}