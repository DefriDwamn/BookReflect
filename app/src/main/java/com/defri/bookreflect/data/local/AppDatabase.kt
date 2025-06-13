package com.defri.bookreflect.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.defri.bookreflect.data.local.converters.StringListConverter

@Database(entities = [BookEntity::class, MoodEntity::class], version = 1)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun moodDao(): MoodDao
}
