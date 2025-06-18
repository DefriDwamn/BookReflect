package com.defri.bookreflect.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreBookSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val booksCollection = firestore.collection("books")
    private fun userBooksRefs(userId: String) =
        firestore.collection("users").document(userId).collection("booksRefs")

    suspend fun addBookToUser(userId: String, bookId: String) {
        val statusData = mapOf("status" to "ADDED")
        userBooksRefs(userId).document(bookId).set(statusData).await()
    }

    suspend fun updateBookStatus(userId: String, bookId: String, status: String) {
        userBooksRefs(userId).document(bookId).update("status", status).await()
    }

    suspend fun getUserBooks(userId: String): List<FirestoreBookDto> {
        val bookIdsStatus = userBooksRefs(userId).get().await().documents.mapNotNull { doc ->
            val status = doc.getString("status")
            if (status != null) doc.id to status else null
        }
        val books = mutableListOf<FirestoreBookDto>()
        for ((bookId, status) in bookIdsStatus) {
            val book = booksCollection.document(bookId).get().await().toObject(
                FirestoreBookDto::class.java
            )
            if (book != null) {
                books.add(book.copy(id = bookId, status = status))
            }
        }
        return books
    }

    suspend fun getGlobalBooks(): List<FirestoreBookDto> {
        return booksCollection.get().await().documents.mapNotNull {
            it.toObject(FirestoreBookDto::class.java)?.copy(id = it.id)
        }
    }

    suspend fun getGlobalBooksPaged(lastDocumentId: String?, pageSize: Int): List<FirestoreBookDto> {
        var query = booksCollection.orderBy("title").limit(pageSize.toLong())
        if (lastDocumentId != null) {
            val lastDocSnapshot = booksCollection.document(lastDocumentId).get().await()
            query = query.startAfter(lastDocSnapshot)
        }
        return query.get().await().documents.mapNotNull {
            it.toObject(FirestoreBookDto::class.java)?.copy(id = it.id)
        }
    }

    suspend fun searchBooks(query: String, pageSize: Int): List<FirestoreBookDto> {
        val searchQuery = query.lowercase()
        return booksCollection
            .whereGreaterThanOrEqualTo("title", searchQuery)
            .whereLessThanOrEqualTo("title", searchQuery + '\uf8ff')
            .limit(pageSize.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { it.toObject(FirestoreBookDto::class.java)?.copy(id = it.id) }
    }
}