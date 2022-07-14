package com.pareekdevansh.cftracker.models

data class Contest(
    val durationSeconds: Int?,
    val frozen: Boolean?,
    val id: Int?,
    val name: String?,
    val phase: String?,
    val relativeTimeSeconds: Int?,
    val startTimeSeconds: Int?,
    val type: String?
)