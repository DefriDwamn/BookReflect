package com.defri.bookreflect.data.local

import androidx.room.*

@Dao
interface MoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mood: MoodEntity)

    @Update
    suspend fun update(mood: MoodEntity)

    @Query("SELECT * FROM moods")
    suspend fun getAll(): List<MoodEntity>
}