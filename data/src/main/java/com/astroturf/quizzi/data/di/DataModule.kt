package com.astroturf.quizzi.data.di

import com.astroturf.quizzi.data.BuildConfig
import com.astroturf.quizzi.data.remote.rest.api.QuizziApi
import com.astroturf.quizzi.data.remote.websocket.service.QuizziWebSocketService
import com.astroturf.quizzi.data.repository.QuizRepositoryImpl
import com.astroturf.quizzi.data.storage.SharedPreferencesStorage
import com.astroturf.quizzi.domain.repository.QuizRepository
import com.astroturf.quizzi.domain.storage.PreferencesStorage
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
    fun provideQuizziApi(okHttpClient: OkHttpClient): QuizziApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuizziApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWebSocketService(): QuizziWebSocketService {
        return QuizziWebSocketService()
    }

    @Provides
    @Singleton
    fun providePreferencesStorage(
        sharedPreferencesStorage: SharedPreferencesStorage
    ): PreferencesStorage = sharedPreferencesStorage

    @Provides
    @Singleton
    fun provideQuizRepository(
        quizziWebSocketService: QuizziWebSocketService,
        api: QuizziApi
    ): QuizRepository {
        return QuizRepositoryImpl(quizziWebSocketService, api)
    }
}