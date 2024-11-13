package com.alicankorkmaz.quizzi.data.remote.api

import com.alicankorkmaz.quizzi.domain.model.Player
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Header

interface QuizApi {
    @POST("player/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Player

    @POST("player/create")
    suspend fun createPlayer(
        @Body request: CreatePlayerRequest
    ): Player
}

data class LoginRequest(
    val id: String
)

data class CreatePlayerRequest(
    val name: String,
    val avatarUrl: String
) 