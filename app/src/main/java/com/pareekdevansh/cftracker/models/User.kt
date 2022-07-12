package com.pareekdevansh.cftracker.models

class User(
    val handle: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val country: String?,
    val city: String?,
    val contribution: Int,
    val rank: String,
    val Rating: Int,
    val maxRank: String,
    val maxRating: Int,
    val lastOnline: Int,
    val registeredOn: Int,
    val friendsOf: Int,
    val avatar: String,
    val titlePhoto: String
)