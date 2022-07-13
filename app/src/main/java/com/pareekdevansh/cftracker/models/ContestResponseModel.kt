package com.pareekdevansh.cftracker.models

data class ContestResponseModel(
    val status: String,
    val contest: List<Contest>
)