package com.defri.bookreflect.domain.repository

import com.defri.bookreflect.domain.model.Mood
import com.defri.bookreflect.core.Result

interface MoodRepository {
    suspend fun saveMood(userId: String, mood: Mood): Result<Unit>
    suspend fun getMoodsByBook(userId: String, bookId: String): Result<List<Mood>>
    suspend fun deleteMood(userId: String, moodId: String): Result<Unit>
}