package com.alicankorkmaz.quizzi.data.di

import com.alicankorkmaz.quizzi.data.remote.WebSocketService
import com.alicankorkmaz.quizzi.data.repository.QuizRepositoryImpl
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
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