package com.defri.bookreflect.domain.usecase.books

import com.defri.bookreflect.data.model.BookStatus
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class UpdateStatusUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookId: String, status: BookStatus) {
        repository.updateBookStatus(bookId, status)
    }
}
