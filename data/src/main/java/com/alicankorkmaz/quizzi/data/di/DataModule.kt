package com.alicankorkmaz.quizzi.data.di

import com.alicankorkmaz.quizzi.data.BuildConfig
import com.alicankorkmaz.quizzi.data.remote.WebSocketService
import com.alicankorkmaz.quizzi.data.remote.api.QuizApi
import com.alicankorkmaz.quizzi.data.repository.QuizRepositoryImpl
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideQuizApi(): QuizApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL.replace("ws", "http"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuizApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(
        webSocketService: WebSocketService,
        api: QuizApi
    ): QuizRepository {
        return QuizRepositoryImpl(webSocketService, api)
    }
}