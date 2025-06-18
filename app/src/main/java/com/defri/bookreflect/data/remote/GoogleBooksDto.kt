package com.defri.bookreflect.data.remote

data class GoogleBooksResponse(
    val items: List<GoogleBookDto>?
)

data class GoogleBookDto(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String?,
    val authors: List<String>?,
    val description: String?,
    val imageLinks: ImageLinks?
)

data class ImageLinks(
    val thumbnail: String?
)
