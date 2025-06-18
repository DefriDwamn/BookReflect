package com.defri.bookreflect.data.remote

import com.defri.bookreflect.domain.model.Book
import javax.inject.Inject

class GoogleBooksSource @Inject constructor(
    private val api: GoogleBooksApi
) {
    suspend fun getBooksPaged(query: String, startIndex: Int, pageSize: Int): List<GoogleBookDto> {
        return try {
            val response = api.searchBooks(query, "books",  startIndex, pageSize)
            response.items.orEmpty()
        } catch (e: Exception) {
            emptyList()
        }
    }
}