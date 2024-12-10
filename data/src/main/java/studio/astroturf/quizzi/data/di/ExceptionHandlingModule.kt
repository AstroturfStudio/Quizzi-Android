package studio.astroturf.quizzi.data.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import studio.astroturf.quizzi.data.exceptionhandling.AuthExceptionStrategy
import studio.astroturf.quizzi.data.exceptionhandling.DefaultExceptionResolver
import studio.astroturf.quizzi.data.exceptionhandling.GameExceptionStrategy
import studio.astroturf.quizzi.data.exceptionhandling.HttpExceptionStrategy
import studio.astroturf.quizzi.data.exceptionhandling.UnexpectedExceptionStrategy
import studio.astroturf.quizzi.data.exceptionhandling.WebSocketExceptionStrategy
import studio.astroturf.quizzi.domain.exceptionhandling.ExceptionResolver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExceptionHandlingModule {
    @Binds
    @Singleton
    abstract fun bindExceptionResolver(defaultExceptionResolver: DefaultExceptionResolver): ExceptionResolver

    companion object {
        @Provides
        @Singleton
        fun provideAuthExceptionStrategy(): AuthExceptionStrategy = AuthExceptionStrategy()

        @Provides
        @Singleton
        fun provideGameExceptionStrategy(): GameExceptionStrategy = GameExceptionStrategy()

        @Provides
        @Singleton
        fun provideHttpExceptionStrategy(): HttpExceptionStrategy = HttpExceptionStrategy()

        @Provides
        @Singleton
        fun provideWebSocketExceptionStrategy(): WebSocketExceptionStrategy = WebSocketExceptionStrategy()

        @Provides
        @Singleton
        fun provideUnexpectedExceptionStrategy(): UnexpectedExceptionStrategy = UnexpectedExceptionStrategy()
    }
}
