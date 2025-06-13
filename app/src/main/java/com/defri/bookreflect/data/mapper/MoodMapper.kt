package com.defri.bookreflect.data.mapper

import com.defri.bookreflect.data.local.MoodEntity
import com.defri.bookreflect.data.remote.FirestoreMoodDto
import com.defri.bookreflect.domain.model.Mood

object MoodMapper {
    fun toDto(domain: Mood): FirestoreMoodDto {
        return FirestoreMoodDto(
            domain.id,
            domain.bookId,
            domain.tag,
            domain.note,
            domain.quotes,
            domain.createdAt
        )
    }
    fun fromDto(dto: FirestoreMoodDto): Mood {
        return Mood(
            dto.id,
            dto.bookId,
            dto.tag,
            dto.note,
            dto.quotes,
            dto.createdAt
        )
    }
    fun toEntity(domain: Mood): MoodEntity {
        return MoodEntity(
            domain.id,
            domain.bookId,
            domain.tag,
            domain.note,
            domain.quotes,
            domain.createdAt
        )
    }
    fun fromEntity(entity: MoodEntity): Mood {
        return Mood(
            entity.id,
            entity.bookId,
            entity.tag,
            entity.note,
            entity.quotes,
            entity.createdAt
        )
    }
}
