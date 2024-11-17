package com.astroturf.quizzi.domain.di

import com.astroturf.quizzi.domain.repository.QuizRepository
import com.astroturf.quizzi.domain.usecase.ObserveQuizUseCase
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