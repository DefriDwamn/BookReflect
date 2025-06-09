package com.defri.bookreflect.domain.usecase.books

import com.defri.bookreflect.data.model.Book
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(book: Book) {
        repository.addBook(book)
    }
}
