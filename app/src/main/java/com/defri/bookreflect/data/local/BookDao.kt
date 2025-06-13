package com.defri.bookreflect.data.local

import androidx.room.*

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    @Update
    suspend fun update(book: BookEntity)

    @Query("SELECT * FROM books")
    suspend fun getAll(): List<BookEntity>

    @Query("SELECT * FROM books WHERE id = :bookId")
    suspend fun getById(bookId: String): BookEntity?
}