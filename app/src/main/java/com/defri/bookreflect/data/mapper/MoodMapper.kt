package com.defri.bookreflect.data.mapper

import com.defri.bookreflect.data.local.MoodEntity
import com.defri.bookreflect.data.remote.FirestoreMoodDto
import com.defri.bookreflect.domain.model.Mood
import java.util.UUID

object MoodMapper {
    fun toDto(domain: Mood): FirestoreMoodDto {
        return FirestoreMoodDto(
            domain.id,
            domain.bookId,
            tags = domain.tags,
            note = domain.note,
            quotes = domain.quotes,
            createdAt = domain.createdAt
        )
    }
    fun fromDto(dto: FirestoreMoodDto): Mood {
        return Mood(
            dto.id,
            dto.bookId,
            dto.tags,
            dto.note,
            dto.quotes,
            false,
            dto.createdAt
        )
    }
    fun toEntity(domain: Mood): MoodEntity {
        return MoodEntity(
            domain.id.ifBlank { UUID.randomUUID().toString() },
            domain.bookId,
            domain.tags,
            domain.note,
            domain.quotes,
            domain.isLocal,
            domain.createdAt
        )
    }
    fun fromEntity(entity: MoodEntity): Mood {
        return Mood(
            entity.id,
            entity.bookId,
            entity.tags,
            entity.note,
            entity.quotes,
            entity.isLocal,
            entity.createdAt
        )
    }
}
