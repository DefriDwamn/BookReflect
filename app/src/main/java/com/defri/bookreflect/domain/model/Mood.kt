package com.defri.bookreflect.domain.model

data class Mood(
    val id: String,
    val bookId: String,
    val tags: List<String>,
    val note: String,
    val quotes: List<String>,
    val isLocal: Boolean,
    val createdAt: Long
)
