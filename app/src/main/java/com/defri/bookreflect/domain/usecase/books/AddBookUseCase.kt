package com.defri.bookreflect.domain.usecase.books

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(userId: String, bookId: String): Result<Unit> {
        return repository.addBook(userId, bookId)
    }
}
