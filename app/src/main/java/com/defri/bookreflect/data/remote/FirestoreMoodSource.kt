package com.defri.bookreflect.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreMoodSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val moodsCollection = firestore.collection("moods")

    suspend fun saveMood(userId: String, mood: FirestoreMoodDto) {
        val moodWithUserId = mood.copy(userId = userId)
        if (mood.id.isBlank()) {
            val docRef = moodsCollection.document()
            val moodWithId = moodWithUserId.copy(id = docRef.id)
            docRef.set(moodWithId).await()
        } else {
            moodsCollection.document(mood.id).set(moodWithUserId).await()
        }
    }

    suspend fun getUserMoods(userId: String): List<FirestoreMoodDto> {
        return moodsCollection
            .whereEqualTo("userId", userId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(FirestoreMoodDto::class.java)?.copy(id = it.id) }
    }

    suspend fun getMoodsByBook(userId: String, bookId: String): List<FirestoreMoodDto> {
        return moodsCollection
            .whereEqualTo("userId", userId)
            .whereEqualTo("bookId", bookId)
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(FirestoreMoodDto::class.java)?.copy(id = it.id) }
    }

    suspend fun deleteMood(moodId: String) {
        moodsCollection.document(moodId).delete().await()
    }
}