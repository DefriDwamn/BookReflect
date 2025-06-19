package com.defri.bookreflect.domain.usecase.moods

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.model.Mood
import com.defri.bookreflect.domain.repository.MoodRepository
import javax.inject.Inject

class GetMoodsByBookUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    suspend operator fun invoke(userId: String, bookId: String): Result<List<Mood>> {
        return repository.getMoodsByBook(userId, bookId)
    }
}