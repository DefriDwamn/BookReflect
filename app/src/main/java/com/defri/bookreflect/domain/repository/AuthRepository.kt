package com.defri.bookreflect.domain.repository

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.presentation.profile.ProfileData
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun updateUserName(name: String): Result<Unit>
    suspend fun getUserProfile(): Result<ProfileData>
    fun logout()
    fun getCurrentUser(): FirebaseUser?
}