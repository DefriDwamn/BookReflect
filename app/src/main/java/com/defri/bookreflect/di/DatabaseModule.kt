package com.defri.bookreflect.di

import android.content.Context
import androidx.room.Room
import com.defri.bookreflect.data.local.AppDatabase
import com.defri.bookreflect.data.local.BookDao
import com.defri.bookreflect.data.local.MoodDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "bookreflect.db").build()

    @Provides fun provideBookDao(db: AppDatabase): BookDao = db.bookDao()
    @Provides fun provideMoodDao(db: AppDatabase): MoodDao = db.moodDao()
}