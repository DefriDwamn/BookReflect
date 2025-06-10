package com.defri.bookreflect.data.model

import com.google.firebase.firestore.DocumentId

enum class BookStatus {
    ADDED,
    COMPLETED
}

data class Book(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val status: BookStatus = BookStatus.ADDED
)
