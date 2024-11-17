package com.astroturf.quizzi.data.di

import com.astroturf.quizzi.data.BuildConfig
import com.astroturf.quizzi.data.remote.rest.api.QuizziApi
import com.astroturf.quizzi.data.remote.rest.service.QuizziApiService
import com.astroturf.quizzi.data.remote.websocket.service.QuizziWebSocketService
import com.astroturf.quizzi.data.repository.QuizRepositoryImpl
import com.astroturf.quizzi.data.storage.SharedPreferencesStorage
import com.astroturf.quizzi.domain.repository.QuizRepository
import com.astroturf.quizzi.domain.storage.PreferencesStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    @RestClient
    fun provideRestHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @WebSocketClient
    fun provideWebSocketHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(5, TimeUnit.SECONDS)
            .pingInterval(20, TimeUnit.SECONDS)
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            classDiscriminator = "type"
            isLenient = true
        }
    }

    @Provides
    @Singleton
    fun provideQuizziApi(@RestClient okHttpClient: OkHttpClient): QuizziApi {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuizziApi::class.java)
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
        apiService: QuizziApiService
    ): QuizRepository {
        return QuizRepositoryImpl(quizziWebSocketService, apiService)
    }
}