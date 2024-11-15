package com.alicankorkmaz.quizzi.data.di

import com.alicankorkmaz.quizzi.data.BuildConfig
import com.alicankorkmaz.quizzi.data.remote.WebSocketService
import com.alicankorkmaz.quizzi.data.remote.api.QuizziApi
import com.alicankorkmaz.quizzi.data.repository.QuizRepositoryImpl
import com.alicankorkmaz.quizzi.domain.repository.QuizRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideQuizApi(
        okHttpClient: OkHttpClient
    ): QuizziApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuizziApi::class.java)
    }

    @Provides
    @Singleton
    fun provideQuizRepository(
        webSocketService: WebSocketService,
        api: QuizziApi
    ): QuizRepository {
        return QuizRepositoryImpl(webSocketService, api)
    }
}