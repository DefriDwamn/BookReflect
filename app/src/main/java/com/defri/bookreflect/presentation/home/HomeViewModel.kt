package com.defri.bookreflect.presentation.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.repository.AuthRepository
import com.defri.bookreflect.domain.usecase.books.CreateBookUseCase
import com.defri.bookreflect.domain.usecase.books.GetAllBooksUseCase
import com.defri.bookreflect.domain.usecase.books.GetUserBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllBooksUseCase: GetAllBooksUseCase,
    private val getUserBooksUseCase: GetUserBooksUseCase,
    private val createBookUseCase: CreateBookUseCase,
    private val authRepository: AuthRepository
) : BaseViewModel<HomeState, HomeEvent>() {
    override fun initialState(): HomeState = HomeState()

    companion object {
        private const val PAGE_SIZE = 20
    }
    private var lastGlobalBookId: String? = null

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.LoadData -> {
                resetPagination()
                loadInitialData()
            }
            HomeEvent.LoadMoreGlobalBooks -> {
                loadMoreGlobalBooks()
            }
            is HomeEvent.AddBook -> addBook(event.book)
            is HomeEvent.UpdateSearchQuery -> setState { copy(searchQuery = event.query) }
        }
    }

    private fun resetPagination() {
        lastGlobalBookId = null
        setState { copy(globalBooks = emptyList(), error = null, isEndReached = false, isLoadingMore = false) }
    }

    private fun loadInitialData() {
        launchWithLoading(
            onStart = { copy(isLoading = true, error = null, isEndReached = false, isLoadingMore = false) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val uid = authRepository.getCurrentUser()?.uid
            if (uid == null) {
                setState { copy(error = "not logged in") }
                return@launchWithLoading
            }
            when (val globalRes = getAllBooksUseCase(lastGlobalBookId, PAGE_SIZE)) {
                is Result.Success -> {
                    val all = globalRes.data ?: emptyList()
                    lastGlobalBookId = all.lastOrNull()?.id
                    val endReached = all.size < PAGE_SIZE
                    setState { copy(globalBooks = all, isEndReached = endReached) }
                }
                is Result.Error -> {
                    setState { copy(error = globalRes.exception.message) }
                }
                else -> {}
            }
            when (val userRes = getUserBooksUseCase(uid)) {
                is Result.Success -> {
                    val ulist = userRes.data ?: emptyList()
                    setState { copy(userBooks = ulist) }
                }
                is Result.Error -> {
                    setState { copy(error = userRes.exception.message) }
                }
                else -> {}
            }
        }
    }

    private fun loadMoreGlobalBooks() {
        if (state.value.isLoadingMore || state.value.isEndReached) return
        setState { copy(isLoadingMore = true, error = null) }
        viewModelScope.launch {
            val uid = authRepository.getCurrentUser()?.uid
            if (uid == null) {
                setState { copy(error = "not logged in", isLoadingMore = false) }
                return@launch
            }
            when (val globalRes = getAllBooksUseCase(lastGlobalBookId, PAGE_SIZE)) {
                is Result.Success -> {
                    val newBooks = globalRes.data ?: emptyList()
                    if (newBooks.isEmpty()) {
                        setState { copy(isEndReached = true, isLoadingMore = false) }
                    } else {
                        lastGlobalBookId = newBooks.lastOrNull()?.id
                        val currentList = state.value.globalBooks.toMutableList()
                        currentList.addAll(newBooks)
                        setState { copy(globalBooks = currentList, isLoadingMore = false) }
                    }
                }
                is Result.Error -> {
                    setState { copy(error = globalRes.exception.message, isLoadingMore = false) }
                }
                else -> {
                    setState { copy(isLoadingMore = false) }
                }
            }
        }
    }

    private fun addBook(book: Book) {
        launchWithLoading(
            onStart = { copy(isLoading = true, error = null) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val uid = authRepository.getCurrentUser()?.uid
            if (uid == null) {
                setState { copy(error = "not logged in") }
                return@launchWithLoading
            }
            val result = createBookUseCase(book.copy(isLocal = false))
            if (result is Result.Success) {
                val currentUserBooks = state.value.userBooks.toMutableList()
                if (currentUserBooks.none { it.id == book.id }) {
                    currentUserBooks.add(book.copy(isLocal = false))
                    setState { copy(userBooks = currentUserBooks) }
                }
            } else if (result is Result.Error) {
                Log.e("HomeViewModel", "adding book: ${result.exception.message}")
                setState { copy(error = result.exception.message) }
            }
        }
    }
}

data class HomeState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isEndReached: Boolean = false,
    val error: String? = null,
    val globalBooks: List<Book> = emptyList(),
    val userBooks: List<Book> = emptyList(),
    val searchQuery: String = ""
)

sealed class HomeEvent {
    object LoadData : HomeEvent()
    object LoadMoreGlobalBooks : HomeEvent()
    data class AddBook(val book: Book) : HomeEvent()
    data class UpdateSearchQuery(val query: String) : HomeEvent()
}