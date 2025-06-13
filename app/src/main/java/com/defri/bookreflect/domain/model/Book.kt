package com.defri.bookreflect.domain.model

enum class BookStatus {
    ADDED,
    COMPLETED
}

data class Book(
    val id: String,
    val isLocal: Boolean,
    val title: String,
    val author: String,
    val description: String,
    val coverUrl: String,
    val status: BookStatus?
)
