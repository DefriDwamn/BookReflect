package com.defri.bookreflect.data.mapper

import com.defri.bookreflect.data.local.BookEntity
import com.defri.bookreflect.data.remote.FirestoreBookDto
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.domain.model.BookStatus

object BookMapper {
    fun fromDto(dto: FirestoreBookDto): Book {
        return Book(
            dto.id,
            dto.title,
            dto.author,
            dto.description,
            dto.coverUrl,
            BookStatus.valueOf(dto.status)
        )
    }
    fun toEntity(domain: Book): BookEntity {
        return BookEntity(
            domain.id,
            domain.title,
            domain.author,
            domain.description,
            domain.coverUrl,
            domain.status.name
        )
    }
    fun fromEntity(entity: BookEntity): Book {
        return Book(
            entity.id,
            entity.title,
            entity.author,
            entity.description,
            entity.coverUrl,
            BookStatus.valueOf(entity.status)
        )
    }
}
