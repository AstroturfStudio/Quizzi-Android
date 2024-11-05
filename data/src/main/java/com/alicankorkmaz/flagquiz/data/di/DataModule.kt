package com.alicankorkmaz.flagquiz.data.di

import com.alicankorkmaz.flagquiz.data.remote.WebSocketService
import com.alicankorkmaz.flagquiz.data.repository.QuizRepositoryImpl
import com.alicankorkmaz.flagquiz.domain.repository.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideQuizRepository(webSocketService: WebSocketService): QuizRepository {
        return QuizRepositoryImpl(webSocketService)
    }
}