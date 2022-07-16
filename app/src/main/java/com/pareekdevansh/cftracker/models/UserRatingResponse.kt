package com.pareekdevansh.cftracker.models

import com.google.gson.annotations.SerializedName

data class UserRatingResponse(
    @SerializedName("result")
    val ratingChangeList : List<RatingChange>,
    val status: String
)