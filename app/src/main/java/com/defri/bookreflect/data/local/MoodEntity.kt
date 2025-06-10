package com.defri.bookreflect.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.defri.bookreflect.data.local.converters.StringListConverter

@Entity(tableName = "moods")
data class MoodEntity(
    @PrimaryKey val id: String,
    val bookId: String,
    val tag: String,
    val note: String,
    @TypeConverters(StringListConverter::class)
    val quotes: List<String>,
    val createdAt: Long
)
