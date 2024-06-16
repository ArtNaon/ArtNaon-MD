package com.example.artnaon.data.pref

data class MessageModel (
    val username: String,
    val message: String,
    val date: String,
    val userProfilePicture: String? = null
)