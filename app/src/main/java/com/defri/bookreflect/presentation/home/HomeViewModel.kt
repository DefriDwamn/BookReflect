package com.defri.bookreflect.presentation.home

import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.core.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : BaseViewModel<HomeState, HomeEvent>() {
    override fun initialState(): HomeState = HomeState()

    override fun handleEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadData -> loadData()
        }
    }

    private fun loadData() {
        launchWithLoading {
            // TODO: Implement data loading logic w repository
            setState { 
                copy(
                    result = Result.Success(
                        HomeData(
                            readingProgress = 0.7f,
                            currentBook = "Current Book",
                            recentBooks = emptyList()
                        )
                    )
                )
            }
        }
    }
}

data class HomeState(
    val result: Result<HomeData> = Result.Success(HomeData())
)

data class HomeData(
    val readingProgress: Float = 0f,
    val currentBook: String = "",
    val recentBooks: List<String> = emptyList()
)

sealed class HomeEvent {
    object LoadData : HomeEvent()
} 