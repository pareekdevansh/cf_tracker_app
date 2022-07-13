package com.pareekdevansh.cftracker.api

import com.pareekdevansh.cftracker.models.ContestResponseModel
import com.pareekdevansh.cftracker.models.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CFApi {

    @GET("contest.list")
    suspend fun getContests(): Response<ContestResponseModel>

    @GET("user.info")
    suspend fun searchUser(
        @Query("handles") userHandles: List<String>
    ): Response<List<User>>
}