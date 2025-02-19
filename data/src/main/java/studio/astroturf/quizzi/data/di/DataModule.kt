package studio.astroturf.quizzi.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import studio.astroturf.quizzi.data.BuildConfig
import studio.astroturf.quizzi.data.remote.rest.api.QuizziApi
import studio.astroturf.quizzi.data.repository.auth.AuthRepositoryImpl
import studio.astroturf.quizzi.data.repository.category.CategoryRepositoryImpl
import studio.astroturf.quizzi.data.repository.game.GameRepositoryImpl
import studio.astroturf.quizzi.data.repository.gametype.GameTypeRepositoryImpl
import studio.astroturf.quizzi.data.repository.rooms.RoomsRepositoryImpl
import studio.astroturf.quizzi.data.storage.SharedPreferencesStorage
import studio.astroturf.quizzi.domain.repository.AuthRepository
import studio.astroturf.quizzi.domain.repository.CategoryRepository
import studio.astroturf.quizzi.domain.repository.GameRepository
import studio.astroturf.quizzi.domain.repository.GameTypeRepository
import studio.astroturf.quizzi.domain.repository.RoomsRepository
import studio.astroturf.quizzi.domain.storage.PreferencesStorage
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    @RestClient
    fun provideRestHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BODY
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            ).connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    @WebSocketClient
    fun provideWebSocketHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) {
                            HttpLoggingInterceptor.Level.BODY
                        } else {
                            HttpLoggingInterceptor.Level.NONE
                        }
                },
            ).connectTimeout(5, TimeUnit.SECONDS)
            .pingInterval(1, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideJson(): Json =
        Json {
            ignoreUnknownKeys = true
            classDiscriminator = "type"
            isLenient = true
        }

    @Provides
    @Singleton
    fun provideQuizziApi(
        @RestClient okHttpClient: OkHttpClient,
        json: Json,
    ): QuizziApi =
        Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json; charset=UTF8".toMediaType()))
            .build()
            .create(QuizziApi::class.java)

    @Provides
    @Singleton
    fun providePreferencesStorage(sharedPreferencesStorage: SharedPreferencesStorage): PreferencesStorage = sharedPreferencesStorage

    @Provides
    @Singleton
    fun provideRoomsRepository(roomsRepositoryImpl: RoomsRepositoryImpl): RoomsRepository = roomsRepositoryImpl

    @Provides
    @Singleton
    fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository = authRepositoryImpl

    @Provides
    @Singleton
    fun provideGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository = gameRepositoryImpl

    @Provides
    @Singleton
    fun provideCategoriesRepository(categoriesRepositoryImpl: CategoryRepositoryImpl): CategoryRepository = categoriesRepositoryImpl

    @Provides
    @Singleton
    fun provideGameTypeRepository(gameTypeRepositoryImpl: GameTypeRepositoryImpl): GameTypeRepository = gameTypeRepositoryImpl
}
