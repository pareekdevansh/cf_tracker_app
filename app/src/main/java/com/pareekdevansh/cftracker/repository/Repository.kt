package com.pareekdevansh.cftracker.repository

import com.pareekdevansh.cftracker.api.RetrofitInstance
import com.pareekdevansh.cftracker.models.ContestResponseModel
import com.pareekdevansh.cftracker.models.RatingChangeResponse
import com.pareekdevansh.cftracker.models.UserResponseModel
import retrofit2.Response

class Repository {
    val cfApi = RetrofitInstance.api
    suspend fun getContest() : Response<ContestResponseModel>{
        return cfApi.getContest()
    }

    suspend fun getUser(list: List<String>): Response<UserResponseModel>{
        return cfApi.getUser(list)
    }

    suspend fun getRatingChange(contestId : Int ) : Response<RatingChangeResponse>{
        return cfApi.getRatingChanges(contestId)
    }
}