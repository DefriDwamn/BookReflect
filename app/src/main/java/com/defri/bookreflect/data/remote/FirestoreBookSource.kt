package com.defri.bookreflect.data.remote

import com.defri.bookreflect.data.model.Book
import com.defri.bookreflect.data.model.BookStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreBookSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val booksCollection = firestore.collection("books")

    suspend fun addBook(book: Book) {
        booksCollection.add(book).await()
    }

    suspend fun updateBookStatus(bookId: String, status: BookStatus) {
        booksCollection.document(bookId)
            .update("status", status.name)
            .await()
    }

    suspend fun getBooks(): List<Book> {
        val snapshot = booksCollection.get().await()
        return snapshot.documents.mapNotNull { it.toObject(Book::class.java)?.copy(id = it.id) }
    }
}
