package com.defri.bookreflect.data.repository

import com.defri.bookreflect.data.model.Book
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.data.model.BookStatus
import com.defri.bookreflect.data.remote.FirestoreBookSource
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val firestoreBookSource: FirestoreBookSource
) : BookRepository {

    override suspend fun addBook(book: Book): Result<Unit> {
        return try {
            firestoreBookSource.addBook(book)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateBookStatus(bookId: String, status: BookStatus): Result<Unit> {
        return try {
            firestoreBookSource.updateBookStatus(bookId, status)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getBooks(): Result<List<Book>> {
        return try {
            val books = firestoreBookSource.getBooks()
            Result.Success(books)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

