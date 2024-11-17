package com.astroturf.quizzi.data.remote.rest.api

import com.astroturf.quizzi.data.remote.rest.model.CreatePlayerRequestDto
import com.astroturf.quizzi.data.remote.rest.model.LoginRequestDto
import com.astroturf.quizzi.data.remote.rest.model.RoomsDto
import com.astroturf.quizzi.data.remote.websocket.model.PlayerDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface QuizziApi {
    @POST("api/player/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): PlayerDto

    @POST("api/player/create")
    suspend fun createPlayer(
        @Body request: CreatePlayerRequestDto
    ): PlayerDto

    @GET("api/room/all")
    suspend fun getRooms(): RoomsDto
}