package com.defri.bookreflect.data.remote

import com.google.firebase.firestore.DocumentId

data class FirestoreBookDto(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val description: String = "",
    val coverUrl: String = "",
    val status: String = ""
)
