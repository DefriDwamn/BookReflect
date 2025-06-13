package com.defri.bookreflect.data.repository

import android.util.Log
import com.defri.bookreflect.domain.model.Book
import com.defri.bookreflect.core.Result
import com.defri.bookreflect.data.local.BookDao
import com.defri.bookreflect.data.mapper.BookMapper
import com.defri.bookreflect.domain.model.BookStatus
import com.defri.bookreflect.data.remote.FirestoreBookSource
import com.defri.bookreflect.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val firestoreBookSource: FirestoreBookSource,
    private val dao: BookDao
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
                Log.e("getUserBooks", "Remote fetch failed: ${e.message}")
                emptyList()
            }
            Result.Success(local + remote)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAllBooks(): Result<List<Book>> {
        return try {
            val snapshot = firestoreBookSource.getGlobalBooks()
            val books = snapshot.map { BookMapper.fromDto(it).copy(isLocal = false) }
            Result.Success(books)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

}

