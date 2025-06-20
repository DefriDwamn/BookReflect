package com.defri.bookreflect.data.repository

import android.util.Log
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.data.local.BookDao
import com.defri.bookreflect.data.mapper.BookMapper
import com.defri.bookreflect.domain.model.BookStatus
import com.defri.bookreflect.data.remote.FirestoreBookSource
import com.defri.bookreflect.data.remote.GoogleBooksSource
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val firestoreBookSource: FirestoreBookSource,
    private val dao: BookDao,
    private val googleBooksSource: GoogleBooksSource
) : BookRepository {

    override suspend fun createBook(book: Book): Result<Unit> {
        return try {
            dao.insert(BookMapper.toEntity(book))
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun addBook(userId: String, bookId: String): Result<Unit> {
        return try {
            firestoreBookSource.addBookToUser(userId, bookId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateBookStatus(userId: String, book: Book, status: BookStatus): Result<Unit> {
        return try {
            if (book.isLocal) {
                val entity = dao.getById(book.id)
                if (entity != null) {
                    dao.update(entity.copy(status = status.name))
                }
            } else {
                firestoreBookSource.updateBookStatus(userId, book.id, status.name)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserBooks(userId: String): Result<List<Book>> {
        return try {
            val local = dao.getAll().map { BookMapper.fromEntity(it).copy(isLocal = true) }
            val remote = try {
                firestoreBookSource.getUserBooks(userId).map {
                    BookMapper.fromDto(it).copy(isLocal = false)
                }
            } catch (e: Exception) {
                Log.e("getUserBooks", "fetch failed: ${e.message}")
                emptyList()
            }
            Result.Success(local + remote)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteBook(userId: String, bookId: String, isLocal: Boolean): Result<Unit> {
        return try {
            // TODO: check isLocal
            dao.delete(bookId)
            firestoreBookSource.deleteBookFromUser(userId, bookId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getGlobalBooksPaged(lastDocumentId: String?, pageSize: Int): Result<List<Book>> {
        return try {
            val firestoreBooks = try {
                firestoreBookSource.getGlobalBooksPaged(lastDocumentId, pageSize)
                    .map { BookMapper.fromDto(it).copy(isLocal = false) }
            } catch (e: Exception) {
                emptyList()
            }
            val needSize = pageSize - firestoreBooks.size
            val googleBooks = if (needSize > 0) {
                try {
                    val randomQuery = getRandomQuery()
                    val randomStartIndex = (0..100).random()
                    googleBooksSource.getBooksPaged(
                        query = randomQuery,
                        startIndex = randomStartIndex,
                        pageSize = needSize
                    ).map { BookMapper.fromGoogleDto(it) }
                } catch (e: Exception) {
                    emptyList()
                }
            } else emptyList()
            Result.Success(firestoreBooks + googleBooks)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun searchBooks(query: String, pageSize: Int): Result<List<Book>> {
        return try {
            val firestoreBooks = try {
                firestoreBookSource.searchBooks(query, pageSize)
                    .map { BookMapper.fromDto(it).copy(isLocal = false) }
            } catch (e: Exception) {
                emptyList()
            }
            val needSize = pageSize - firestoreBooks.size
            val googleBooks = if (needSize > 0) {
                try {
                    googleBooksSource.getBooksPaged(
                        query = query,
                        startIndex = 0,
                        pageSize = needSize
                    ).map { BookMapper.fromGoogleDto(it) }
                } catch (e: Exception) {
                    emptyList()
                }
            } else emptyList()
            Result.Success(firestoreBooks + googleBooks)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun getRandomQuery(): String {
        val queries = listOf(
            "drama", "fantasy", "fiction", "history",
            "philosophy", "adventure", "horror"
        )
        val count = (1..1).random()
        val selectedGenres = queries.shuffled().take(count)
        return "subject:" + selectedGenres.joinToString("+")
    }
}

