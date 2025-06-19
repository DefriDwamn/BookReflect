package com.defri.bookreflect.presentation.books

import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.BookStatus
import com.defri.bookreflect.domain.model.Mood
import com.defri.bookreflect.domain.repository.AuthRepository
import com.defri.bookreflect.domain.usecase.books.DeleteBookUseCase
import com.defri.bookreflect.domain.usecase.books.GetUserBooksUseCase
import com.defri.bookreflect.domain.usecase.books.UpdateStatusUseCase
import com.defri.bookreflect.domain.usecase.moods.DeleteMoodUseCase
import com.defri.bookreflect.domain.usecase.moods.GetMoodsByBookUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val updateStatusUseCase: UpdateStatusUseCase,
    private val getUserBooksUseCase: GetUserBooksUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val deleteMoodUseCase: DeleteMoodUseCase,
    private val getMoodsByBookUseCase: GetMoodsByBookUseCase,
    private val authRepository: AuthRepository
) : BaseViewModel<BooksState, BooksEvent>() {
    private fun getUserId() = authRepository.getCurrentUser()?.uid

    override fun initialState(): BooksState = BooksState()

    override fun handleEvent(event: BooksEvent) {
        when (event) {
            BooksEvent.LoadBooks -> loadBooks()
            is BooksEvent.UpdateStatus -> updateStatus(event.book, event.status)
            is BooksEvent.DeleteBook -> deleteBook(event.book)
        }
    }

    private fun loadBooks() {
        launchWithLoading(
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val uid = getUserId() ?: return@launchWithLoading
            val books = getUserBooksUseCase(uid)
            if (books is Result.Success && books.data != null) {
                setState { copy(books = books.data) }
            } else if (books is Result.Error) {
                setState { copy(error = books.exception.message) }
            }
        }
    }

    private fun updateStatus(book: Book, status: BookStatus) {
        launchWithLoading(
            onStart = { copy(isLoading = true) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val uid = getUserId() ?: return@launchWithLoading
            val result = updateStatusUseCase(uid, book, status)
            if (result is Result.Success) {
                loadBooks()
            } else if (result is Result.Error) {
                setState { copy(error = result.exception.message) }
            }
        }
    }

    private fun deleteBook(book: Book) {
        launchWithLoading(
            onStart = { copy(isLoading = true) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val uid = getUserId() ?: return@launchWithLoading
            val deleteBookResult = deleteBookUseCase(uid, book.id, book.isLocal)
            if (deleteBookResult is Result.Error) {
                setState { copy(error = deleteBookResult.exception.message) }
                return@launchWithLoading
            }
            if (!book.isLocal) {
                val moodsResult = getMoodsByBookUseCase(uid, book.id)
                if (moodsResult is Result.Success) {
                    moodsResult.data?.forEach { mood ->
                        deleteMoodUseCase(uid, mood.id)
                    }
                }
            }
            loadBooks()
        }
    }
}

data class BooksState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val books: List<Book> = emptyList(),
    val moods: List<Mood> = emptyList()
)

sealed class BooksEvent {
    object LoadBooks : BooksEvent()
    data class DeleteBook(val book: Book) : BooksEvent()
    data class UpdateStatus(val book: Book, val status: BookStatus) : BooksEvent()
}