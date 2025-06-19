package com.defri.bookreflect.domain.usecase.moods

import com.defri.bookreflect.core.Result
import com.defri.bookreflect.domain.model.Mood
import com.defri.bookreflect.domain.repository.MoodRepository
import javax.inject.Inject

class SaveMoodUseCase @Inject constructor(
    private val repository: MoodRepository
) {
    suspend operator fun invoke(userId: String, mood: Mood): Result<Mood> {
        return repository.saveMood(userId, mood)
    }
}