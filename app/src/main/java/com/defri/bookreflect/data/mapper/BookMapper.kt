package com.defri.bookreflect.data.mapper

import com.defri.bookreflect.data.local.BookEntity
import com.defri.bookreflect.data.remote.FirestoreBookDto
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.BookStatus
import java.util.UUID

object BookMapper {
    fun toDto(domain: Book): FirestoreBookDto {
        return FirestoreBookDto(
            domain.id,
            domain.title,
            domain.author,
            domain.description,
            domain.coverUrl,
            domain.status?.name ?: ""
        )
    }
    fun fromDto(dto: FirestoreBookDto): Book {
        return Book(
            dto.id,
            false,
            dto.title,
            dto.author,
            dto.description,
            dto.coverUrl,
            if (dto.status.isBlank()) null else BookStatus.valueOf(dto.status),
        )
    }
    fun toEntity(domain: Book): BookEntity {
        return BookEntity(
            domain.id.ifBlank { UUID.randomUUID().toString() },
            domain.isLocal,
            domain.title,
            domain.author,
            domain.description,
            domain.coverUrl,
            domain.status?.name ?: ""
        )
    }
    fun fromEntity(entity: BookEntity): Book {
        return Book(
            entity.id,
            entity.isLocal,
            entity.title,
            entity.author,
            entity.description,
            entity.coverUrl,
            if (entity.status.isBlank()) null else BookStatus.valueOf(entity.status),
        )
    }
}
