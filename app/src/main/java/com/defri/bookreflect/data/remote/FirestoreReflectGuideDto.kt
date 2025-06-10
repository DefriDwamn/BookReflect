package com.defri.bookreflect.data.remote

import com.google.firebase.firestore.DocumentId

data class FirestoreReflectGuideDto(
    @DocumentId
    val id: String = "",
    val text: String = "",
)
