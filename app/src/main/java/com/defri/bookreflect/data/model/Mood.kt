package com.defri.bookreflect.data.model
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName

data class Mood(
    @DocumentId
    val id: String = "",
    val bookId: String = "",
    val tag: String = "",
    val note: String = "",
    val quotes: List<String> = emptyList(),
    @get:PropertyName("created_at")
    @set:PropertyName("created_at")
    var createdAt: Long = System.currentTimeMillis()
)