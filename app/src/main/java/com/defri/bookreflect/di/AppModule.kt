package com.defri.bookreflect.di

import com.defri.bookreflect.data.local.BookDao
import com.defri.bookreflect.data.remote.FirebaseAuthSource
import com.defri.bookreflect.data.remote.FirestoreBookSource
import com.defri.bookreflect.data.remote.GoogleBooksService
import com.defri.bookreflect.data.repository.AuthRepositoryImpl
import com.defri.bookreflect.data.repository.BookRepositoryImpl
import com.defri.bookreflect.domain.repository.AuthRepository
import com.defri.bookreflect.domain.repository.BookRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = Firebase.firestore

    @Provides
    @Singleton
    fun provideFirebaseAuthSource(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): FirebaseAuthSource = FirebaseAuthSource(firestore, auth)

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuthSource: FirebaseAuthSource
    ): AuthRepository = AuthRepositoryImpl(firebaseAuthSource)

    @Provides
    @Singleton
    fun provideFirestoreBookSource(firestore: FirebaseFirestore): FirestoreBookSource =
        FirestoreBookSource(firestore)

    @Provides
    @Singleton
    fun provideGoogleBooksService(): GoogleBooksService =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/books/v1/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GoogleBooksService::class.java)

    @Provides
    @Singleton
    fun provideBookRepository(
        firestoreBookSource: FirestoreBookSource,
        bookDao: BookDao
    ): BookRepository = BookRepositoryImpl(firestoreBookSource, bookDao)
}
