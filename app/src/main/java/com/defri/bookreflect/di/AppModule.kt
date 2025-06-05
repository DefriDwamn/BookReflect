package com.defri.bookreflect.di

import com.defri.bookreflect.data.remote.FirebaseAuthSource
import com.defri.bookreflect.data.repository.AuthRepositoryImpl
import com.defri.bookreflect.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuthSource(): FirebaseAuthSource = FirebaseAuthSource()

    @Provides
    @Singleton
    fun provideAuthRepository(firebaseAuthSource: FirebaseAuthSource): AuthRepository =
        AuthRepositoryImpl(firebaseAuthSource)
}