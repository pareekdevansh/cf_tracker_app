package com.pareekdevansh.cftracker.models

import com.google.gson.annotations.SerializedName

data class UserResponseModel(
    @SerializedName("result")
    val user: List<User>,
    val status: String
)