package com.pareekdevansh.cftracker.repository

import com.pareekdevansh.cftracker.api.RetrofitInstance
import com.pareekdevansh.cftracker.models.ContestResponseModel
import com.pareekdevansh.cftracker.models.UserResponseModel
import retrofit2.Response

class Repository {
    suspend fun getContest() : Response<ContestResponseModel>{
        return RetrofitInstance.api.getContest()
    }

    suspend fun getUser(list: List<String>): Response<UserResponseModel>{
        return RetrofitInstance.api.getUser(list)
    }
}