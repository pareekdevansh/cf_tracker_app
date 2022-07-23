package com.pareekdevansh.cftracker.models

import com.google.gson.annotations.SerializedName

data class SubmissionsResponse(
    @SerializedName("result")
    val submissions : List<Submission>,
    val status: String
)