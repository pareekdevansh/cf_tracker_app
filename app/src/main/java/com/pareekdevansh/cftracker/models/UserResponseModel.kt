package com.pareekdevansh.cftracker.models

data class UserResponseModel(
    val user: List<User>,
    val status: String
)