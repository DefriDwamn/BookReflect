package com.defri.bookreflect.data.repository

import android.util.Log
import com.defri.bookreflect.data.local.MoodDao
import com.defri.bookreflect.data.mapper.MoodMapper
import com.defri.bookreflect.data.remote.FirestoreMoodSource
import com.defri.bookreflect.domain.model.Mood
import com.defri.bookreflect.domain.repository.MoodRepository
import com.defri.bookreflect.core.Result
import javax.inject.Inject

class MoodRepositoryImpl @Inject constructor(
    private val firestoreMoodSource: FirestoreMoodSource,
    private val dao: MoodDao
) : MoodRepository {

    override suspend fun saveMood(userId: String, mood: Mood): Result<Unit> {
        return try {
            val entity = MoodMapper.toEntity(mood)
            dao.insert(entity)
            // TODO: move this to sync methods (ident sync need to do with books)
            // val moodWithId = MoodMapper.fromEntity(entity);
            // val dto = MoodMapper.toDto(moodWithId).copy(userId = userId)
            // firestoreMoodSource.saveMood(userId, dto)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("saveMood", "saving mood: ${e.message}")
            Result.Error(e)
        }
    }


    override suspend fun getMoodsByBook(userId: String, bookId: String): Result<List<Mood>> {
        return try {
            val sourceMoods = try {
                firestoreMoodSource.getMoodsByBook(userId, bookId)
            } catch (e: Exception) {
                Log.e("getMoodsByBook", "fetch failed: ${e.message}")
                emptyList()
            }
            val entityMoods = dao.getByBookId(bookId)
            val allMoods = (
                    entityMoods.map { MoodMapper.fromEntity(it) } +
                    sourceMoods.map { MoodMapper.fromDto(it) }
                    ).distinctBy { it.id }
            Result.Success(allMoods)
        } catch (e: Exception) {
            Log.e("getMoodsByBook", "fetch moods: ${e.message}")
            Result.Error(e)
        }
    }

    override suspend fun deleteMood(userId: String, moodId: String): Result<Unit> {
        return try {
            dao.delete(moodId)
            firestoreMoodSource.deleteMood(moodId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("deleteMood", "deleting mood: ${e.message}")
            Result.Error(e)
        }
    }

}