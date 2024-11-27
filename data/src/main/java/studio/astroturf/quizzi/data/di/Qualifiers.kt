package studio.astroturf.quizzi.data.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RestClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WebSocketClient 
