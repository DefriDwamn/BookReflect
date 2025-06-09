package com.defri.bookreflect.data.repository

import com.defri.bookreflect.data.model.Book
import com.defri.bookreflect.data.model.BookStatus
import com.defri.bookreflect.data.remote.FirestoreBookSource
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val firestoreBookSource: FirestoreBookSource
) : BookRepository {

    override suspend fun addBook(book: Book) {
        firestoreBookSource.addBook(book)
    }

    override suspend fun updateBookStatus(bookId: String, status: BookStatus) {
        firestoreBookSource.updateBookStatus(bookId, status)
    }

    override suspend fun getBooks(): List<Book> {
        return firestoreBookSource.getBooks()
    }
}
