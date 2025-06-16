package com.defri.bookreflect.domain.usecase.books

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class GetAllBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(lastDocumentId: String?, pageSize: Int): Result<List<Book>> {
        return repository.getGlobalBooksPaged(lastDocumentId, pageSize)
    }
}