package com.defri.bookreflect.domain.usecase.books

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.BookStatus
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class UpdateStatusUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(userId: String, book: Book, status: BookStatus): Result<Unit> {
        return repository.updateBookStatus(userId, book, status)
    }
}
