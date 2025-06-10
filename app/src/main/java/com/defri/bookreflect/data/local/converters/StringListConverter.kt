package com.defri.bookreflect.data.local.converters

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isEmpty()) emptyList()
        else value.split("|")
    }
    @TypeConverter
    fun toString(list: List<String>): String {
        return list.joinToString(separator = "|")
    }
}
