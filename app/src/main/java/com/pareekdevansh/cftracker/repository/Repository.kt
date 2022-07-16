package com.pareekdevansh.cftracker.repository

import com.pareekdevansh.cftracker.api.CFApi
import com.pareekdevansh.cftracker.api.RetrofitInstance
import com.pareekdevansh.cftracker.models.ContestResponseModel
import com.pareekdevansh.cftracker.models.RatingChangeResponse
import com.pareekdevansh.cftracker.models.UserRatingResponse
import com.pareekdevansh.cftracker.models.UserResponseModel
import retrofit2.Response

class Repository {
    private val cfApi: CFApi = RetrofitInstance.api
    suspend fun getContest() : Response<ContestResponseModel>{
        return cfApi.getContest()
    }

    suspend fun getUser(list: List<String>): Response<UserResponseModel>{
        return cfApi.getUser(list)
    }

    suspend fun getRatingChange(contestId : Int ) : Response<RatingChangeResponse>{
        return cfApi.getRatingChanges(contestId)
    }

    suspend fun getUserRatings(userHandle :String ) : Response<UserRatingResponse>{
        return cfApi.getUserRatings(userHandle)
    }
}