package com.defri.bookreflect.domain.usecase.moods

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.repository.MoodRepository
import javax.inject.Inject

class DeleteMoodUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    suspend operator fun invoke(userId: String, moodId: String): Result<Unit> {
        return repository.deleteMood(userId, moodId)
    }
}