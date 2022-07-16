package com.pareekdevansh.cftracker.models

import com.google.gson.annotations.SerializedName

data class RatingChangeResponse(
    @SerializedName("result")
    val ratingChange: List<RatingChange>,
    val status: String
)