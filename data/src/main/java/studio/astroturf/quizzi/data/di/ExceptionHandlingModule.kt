package studio.astroturf.quizzi.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import studio.astroturf.quizzi.data.exceptionhandling.DefaultExceptionHandler
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionHandler
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ExceptionHandlingModule {
    @Provides
    @Singleton
    fun provideExceptionHandler(): ExceptionHandler = DefaultExceptionHandler()
}
