package com.defri.bookreflect.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksService {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): GoogleBooksResponse
}

data class GoogleBooksResponse(
    val items: List<GoogleBookItem>?
)

data class GoogleBookItem(
    val id: String,
    val volumeInfo: VolumeInfo
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>?
)
