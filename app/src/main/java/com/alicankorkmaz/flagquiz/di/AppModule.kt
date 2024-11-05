package com.alicankorkmaz.flagquiz.di


import com.alicankorkmaz.flagquiz.data.remote.WebSocketService
import com.alicankorkmaz.flagquiz.domain.repository.QuizRepository
import com.alicankorkmaz.flagquiz.domain.usecase.ObserveQuizUseCase
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
    fun provideWebSocketService(): WebSocketService {
        return WebSocketService()
    }

    @Provides
    @Singleton
    fun provideObserveQuizUseCase(repository: QuizRepository): ObserveQuizUseCase {
        return ObserveQuizUseCase(repository)
    }
} 