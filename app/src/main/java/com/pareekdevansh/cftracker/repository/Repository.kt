package com.pareekdevansh.cftracker.repository

import android.graphics.Color
import com.pareekdevansh.cftracker.api.CFApi
import com.pareekdevansh.cftracker.api.RetrofitInstance
import com.pareekdevansh.cftracker.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    suspend fun getSubmissionsList(userHandle: String) : Response<SubmissionsResponse>{
        return cfApi.getSubmissionsList(userHandle)
    }

}