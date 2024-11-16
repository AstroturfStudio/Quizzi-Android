package com.alicankorkmaz.quizzi.domain.di

import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import com.alicankorkmaz.quizzi.domain.usecase.ObserveQuizUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    @Singleton
    fun provideObserveQuizUseCase(repository: QuizRepository): ObserveQuizUseCase {
        return ObserveQuizUseCase(repository)
    }
} 