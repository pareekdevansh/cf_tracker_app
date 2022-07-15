package com.pareekdevansh.cftracker.models

import com.google.gson.annotations.SerializedName

data class ContestResponseModel(
    val status: String,
    @SerializedName("result")
    val contest: List<Contest>
)