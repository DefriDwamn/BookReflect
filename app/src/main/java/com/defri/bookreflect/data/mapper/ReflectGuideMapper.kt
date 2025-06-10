package com.defri.bookreflect.data.mapper

import com.defri.bookreflect.data.remote.FirestoreReflectGuideDto
import com.defri.bookreflect.domain.model.ReflectGuide

object ReflectGuideMapper {
    fun fromDto(dto: FirestoreReflectGuideDto): ReflectGuide = ReflectGuide(
        dto.id,
        dto.text,
    )
    fun toDto(domain: ReflectGuide): FirestoreReflectGuideDto = FirestoreReflectGuideDto(
        domain.id,
        domain.text,
    )
}
