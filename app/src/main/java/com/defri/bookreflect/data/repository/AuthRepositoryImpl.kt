package com.defri.bookreflect.data.repository

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.data.remote.FirebaseAuthSource
import com.defri.bookreflect.domain.repository.AuthRepository
import com.defri.bookreflect.presentation.profile.ProfileData
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthSource: FirebaseAuthSource,
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuthSource.login(email, password)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = firebaseAuthSource.register(name, email, password)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            firebaseAuthSource.sendPasswordResetEmail(email)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUserName(name: String): Result<Unit> {
        return try {
            firebaseAuthSource.updateUserName(name)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserProfile(): Result<ProfileData> {
        return try {
            val profile = firebaseAuthSource.getUserProfile()
            Result.Success(profile)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun logout() {
        firebaseAuthSource.logout()
    }

    override fun getCurrentUser(): FirebaseUser? = firebaseAuthSource.currentUser
}