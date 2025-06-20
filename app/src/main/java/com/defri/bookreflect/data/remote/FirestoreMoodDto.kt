package com.defri.bookreflect.data.remote
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class FirestoreMoodDto(
    @DocumentId
    val id: String = "",
    val bookId: String = "",
    val userId: String = "",
    val tags: List<String> = emptyList(),
    val note: String = "",
    val quotes: List<String> = emptyList(),
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis()
)
