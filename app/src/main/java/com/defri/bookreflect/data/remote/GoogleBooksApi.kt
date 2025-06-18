package com.defri.bookreflect.data.remote
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("printType") type: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int
    ): GoogleBooksResponse
}
