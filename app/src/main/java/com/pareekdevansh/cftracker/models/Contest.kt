package com.pareekdevansh.cftracker.models

data class Contest(
    val id : Int ,
    val name : String,
    val duration : Int , // in seconds
    val startTime : Int? ,
    val url : String? ,
)
enum class Phase{
    BEFORE,
    CODING,
    PENDING_SYSTEM_TEST,
    SYSTEM_TEST,
    FINISHED
}
