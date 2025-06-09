package com.defri.bookreflect.domain.repository

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.data.model.Book
import com.defri.bookreflect.data.model.BookStatus

interface BookRepository {
    suspend fun addBook(book: Book): Result<Unit>
    suspend fun updateBookStatus(bookId: String, status: BookStatus): Result<Unit>
    suspend fun getBooks(): Result<List<Book>>
}
