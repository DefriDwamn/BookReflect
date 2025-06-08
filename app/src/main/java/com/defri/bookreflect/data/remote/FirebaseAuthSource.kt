package com.defri.bookreflect.data.remote

import com.defri.bookreflect.presentation.profile.ProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): FirebaseUser {
        val authResult = auth.signInWithEmailAndPassword(email, password).await()
        return authResult.user ?: throw Exception("User not found")
    }

    suspend fun register(name: String, email: String, password: String): FirebaseUser {
        val authResult = auth.createUserWithEmailAndPassword(email, password).await()
        val user = authResult.user ?: throw Exception("User not created")
        val userData = hashMapOf(
            "name" to name,
            "email" to email,
        )
        firestore.collection("users")
            .document(user.uid)
            .set(userData)
            .await()
        return user
    }

    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun updateUserName(name: String) {
        val userId = auth.currentUser?.uid
            ?: throw Exception("User not authenticated")
        firestore.collection("users")
            .document(userId)
            .update("name", name)
            .await()
    }

    suspend fun getUserProfile(): ProfileData {
        val user = auth.currentUser
            ?: throw Exception("User not authenticated")
        val document = firestore.collection("users")
            .document(user.uid)
            .get()
            .await()
        // TODO: maybe use other template for name and email
        val name = document.getString("name") ?: "No name"
        val email = user.email ?: "No email"
        return ProfileData(name, email, null)
    }

    fun logout() = auth.signOut()
}