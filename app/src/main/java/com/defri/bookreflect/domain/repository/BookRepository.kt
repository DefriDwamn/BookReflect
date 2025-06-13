package com.defri.bookreflect.domain.repository

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.BookStatus

interface BookRepository {
    suspend fun createBook(book: Book): Result<Unit>
    suspend fun addBook(userId: String, bookId: String): Result<Unit>
    suspend fun updateBookStatus(userId: String, book: Book, status: BookStatus): Result<Unit>
    suspend fun getUserBooks(userId: String): Result<List<Book>>
    suspend fun getAllBooks(): Result<List<Book>>
}
