package com.defri.bookreflect.domain.usecase.books

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.data.model.Book
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class GetBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): Result<List<Book>> {
        return repository.getBooks()
    }
}
