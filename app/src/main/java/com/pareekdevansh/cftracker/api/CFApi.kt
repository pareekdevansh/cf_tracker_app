package com.pareekdevansh.cftracker.api

import com.pareekdevansh.cftracker.models.*
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

    @GET("contest.ratingChanges")
    suspend fun getRatingChanges(
        @Query("contestId") contestID : Int
    ) : Response<RatingChangeResponse>

    // list of all the rated contests given by user
    @GET("user.rating")
    suspend fun getUserRatings(
        @Query("handle") userHandle : String
    ) : Response<UserRatingResponse>

    @GET("user.status")
    suspend fun getSubmissionsList(
        @Query("handle") userHandle: String
    ) : Response<SubmissionsResponse>

}