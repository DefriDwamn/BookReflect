package com.defri.bookreflect.domain.usecase.books

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(userId: String, bookId: String, isLocal: Boolean): Result<Unit> {
        return repository.deleteBook(userId, bookId, isLocal)
    }
}