package com.defri.bookreflect.presentation.moods

import com.defri.bookreflect.core.BaseViewModel
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.model.Mood
import com.defri.bookreflect.domain.repository.AuthRepository
import com.defri.bookreflect.domain.usecase.books.GetUserBooksUseCase
import com.defri.bookreflect.domain.usecase.moods.DeleteMoodUseCase
import com.defri.bookreflect.domain.usecase.moods.GetMoodsByBookUseCase
import com.defri.bookreflect.domain.usecase.moods.SaveMoodUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoodsViewModel @Inject constructor(
    private val saveMoodUseCase: SaveMoodUseCase,
    private val getMoodsByBookUseCase: GetMoodsByBookUseCase,
    private val getUserBooksUseCase: GetUserBooksUseCase,
    private val deleteMoodUseCase: DeleteMoodUseCase,
    private val authRepository: AuthRepository
) : BaseViewModel<MoodsState, MoodsEvent>() {

    private fun getUserId() = authRepository.getCurrentUser()?.uid

    override fun initialState(): MoodsState = MoodsState()

    override fun handleEvent(event: MoodsEvent) {
        when (event) {
            is MoodsEvent.LoadMoods -> loadAllMoods()
            is MoodsEvent.SaveMood -> saveMood(event.moodId, event.bookId, event.tags, event.note, event.quotes)
            is MoodsEvent.DeleteMood -> deleteMood(event.moodId)
            is MoodsEvent.AddQuote -> addQuote(event.quote)
            is MoodsEvent.RemoveQuote -> removeQuote(event.index)
            is MoodsEvent.ToggleTag -> toggleTag(event.tag)
        }
    }


    private fun loadAllMoods() {
        launchWithLoading(
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val uid = getUserId() ?: return@launchWithLoading
            val booksResult = getUserBooksUseCase(uid)
            if (booksResult is Result.Error) {
                setState { copy(error = booksResult.exception.message) }
                return@launchWithLoading
            }
            val books = (booksResult as Result.Success).data ?: emptyList()
            val allMoods = mutableListOf<MoodUi>()
            books.forEach { book ->
                val moodsResult = getMoodsByBookUseCase(uid, book.id)
                if (moodsResult is Result.Success) {
                    val moods = moodsResult.data.orEmpty().map { it.toUi(book.title) }
                    allMoods.addAll(moods)
                }
            }
            setState { copy(moods = allMoods) }
        }
    }

    private fun saveMood(moodId: String?, bookId: String, tags: List<String>, note: String, quotes: List<String>) {
        launchWithLoading(
            onStart = { copy(isLoading = true) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val uid = getUserId() ?: return@launchWithLoading
            val mood = Mood(
                id = moodId ?: "",
                bookId = bookId,
                tags = tags,
                note = note,
                quotes = quotes,
                isLocal = true,
                createdAt = System.currentTimeMillis()
            )
            val result = saveMoodUseCase(uid, mood)
            if (result is Result.Success) {
                val currentMoods = state.value.moods.toMutableList()
                if (moodId != null) {
                    val index = currentMoods.indexOfFirst { it.id == moodId }
                    if (index != -1){
                        val newMood = currentMoods[index].copy(
                            note = mood.note,
                            tags = mood.tags,
                            quotes = mood.quotes
                        )
                        currentMoods[index] = newMood
                        setState { copy(moods = currentMoods) }
                    }
                }
                setState {
                    copy(
                        currentNote = "",
                        currentQuotes = emptyList(),
                        selectedTags = emptySet(),
                    )
                }
            } else if (result is Result.Error) {
                setState { copy(error = result.exception.message) }
            }
        }
    }

    private fun deleteMood(moodId: String) {
        launchWithLoading(
            onStart = { copy(isLoading = true) },
            onError = { copy(isLoading = false, error = it.message) },
            onComplete = { copy(isLoading = false) }
        ) {
            val uid = getUserId() ?: return@launchWithLoading
            val result = deleteMoodUseCase(uid, moodId)
            if (result is Result.Success) {
                setState {
                    copy(
                        moods = moods.filterNot { it.id == moodId }
                    )
                }
            } else if (result is Result.Error) {
                setState { copy(error = result.exception.message) }
            }
        }
    }

    private fun addQuote(quote: String) {
        setState { copy(currentQuotes = currentQuotes + quote) }
    }

    private fun removeQuote(index: Int) {
        setState { copy(currentQuotes = currentQuotes.toMutableList().apply { removeAt(index) }) }
    }

    private fun toggleTag(tag: String) {
        setState {
            copy(
                selectedTags = if (selectedTags.contains(tag)) {
                    selectedTags - tag
                } else {
                    selectedTags + tag
                }
            )
        }
    }

}

data class MoodsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val moods: List<MoodUi> = emptyList(),
    val selectedTags: Set<String> = emptySet(),
    val currentNote: String = "",
    val currentQuotes: List<String> = emptyList(),
    val newQuote: String = ""
)

sealed class MoodsEvent {
    object LoadMoods : MoodsEvent()
    data class SaveMood(
        val moodId: String? = null,
        val bookId: String,
        val tags: List<String>,
        val note: String,
        val quotes: List<String>
    ) : MoodsEvent()

    data class DeleteMood(val moodId: String) : MoodsEvent()
    data class AddQuote(val quote: String) : MoodsEvent()
    data class RemoveQuote(val index: Int) : MoodsEvent()
    data class ToggleTag(val tag: String) : MoodsEvent()
}

data class MoodUi(
    val id: String,
    val bookId: String,
    val bookTitle: String,
    val tags: List<String>,
    val note: String,
    val quotes: List<String>,
    val isLocal: Boolean,
    val createdAt: Long
)

fun Mood.toUi(bookTitle: String) = MoodUi(
    id = id,
    bookId = bookId,
    bookTitle = bookTitle,
    tags = tags,
    note = note,
    quotes = quotes,
    isLocal = isLocal,
    createdAt = createdAt
)