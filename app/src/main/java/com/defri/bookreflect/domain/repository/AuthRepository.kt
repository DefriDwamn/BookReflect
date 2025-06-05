package com.defri.bookreflect.domain.repository

import com.defri.bookreflect.core.common.Result
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(name: String, email: String, password: String): Result<FirebaseUser>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun getCurrentUser(): FirebaseUser?
}