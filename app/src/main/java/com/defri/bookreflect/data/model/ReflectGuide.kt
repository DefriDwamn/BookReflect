package com.defri.bookreflect.data.model
import com.google.firebase.firestore.DocumentId

data class ReflectGuide(
    @DocumentId
    val id: String = "",
    val text: String = "",
)