package com.example.artnaon.data.pref

data class UserModel(
    val name: String,
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
    val picture: String? = null
)