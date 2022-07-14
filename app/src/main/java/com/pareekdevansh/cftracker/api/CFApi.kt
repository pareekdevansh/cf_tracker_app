package com.pareekdevansh.cftracker.api

import com.pareekdevansh.cftracker.models.ContestResponseModel
import com.pareekdevansh.cftracker.models.UserResponseModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CFApi {

    @GET("contest.list")
    suspend fun getContest(): Response<ContestResponseModel>

    @GET("user.info")
    suspend fun getUser(
        @Query("handles") userHandles: List<String>
    ): Response<UserResponseModel>
}