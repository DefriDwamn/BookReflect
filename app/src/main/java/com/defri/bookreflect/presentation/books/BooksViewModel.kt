package com.defri.bookreflect.presentation.books

import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.data.model.Book
import com.defri.bookreflect.data.model.BookStatus
import com.defri.bookreflect.domain.usecase.books.AddBookUseCase
import com.defri.bookreflect.domain.usecase.books.GetBooksUseCase
import com.defri.bookreflect.domain.usecase.books.UpdateStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val addBookUseCase: AddBookUseCase,
    private val updateStatusUseCase: UpdateStatusUseCase,
    private val getBooksUseCase: GetBooksUseCase
) : BaseViewModel<BooksState, BooksEvent>() {

    override fun initialState(): BooksState = BooksState()

    override fun handleEvent(event: BooksEvent) {
        when (event) {
            is BooksEvent.AddBook -> addBook(event.book)
            is BooksEvent.UpdateStatus -> updateStatus(event.bookId, event.status)
            BooksEvent.LoadBooks -> loadBooks()
        }
    }

    private fun loadBooks() {
        launchWithLoading(
            onStart = { copy(isLoading = true) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val books = getBooksUseCase()
            if (books is Result.Success && books.data != null) {
                setState { copy(books = books.data) }
            } else if (books is Result.Error) {
                setState { copy(error = books.exception.message) }
            }
        }
    }

    private fun addBook(book: Book) {
        launchWithLoading(
            onStart = { copy(isLoading = true) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            addBookUseCase(book)
            loadBooks()
        }
    }

    private fun updateStatus(bookId: String, status: BookStatus) {
        launchWithLoading(
            onStart = { copy(isLoading = true) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            updateStatusUseCase(bookId, status)
            loadBooks()
        }
    }
}

data class BooksState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val books: List<Book> = emptyList()
)

sealed class BooksEvent {
    data class AddBook(val book: Book) : BooksEvent()
    data class UpdateStatus(val bookId: String, val status: BookStatus) : BooksEvent()
    object LoadBooks : BooksEvent()
}