package com.example.artnaon.data.api

import com.example.artnaon.data.response.ListPaintingResponse
import com.example.artnaon.data.response.LoginResponse
import com.example.artnaon.data.response.RegisterResponse
import com.example.artnaon.data.response.ResetPasswordResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("reset")
    suspend fun resetPassword(
        @Field("email") email: String,
        @Field("newPassword") newPassword: String
    ): ResetPasswordResponse

    @GET("homePage")
    suspend fun getHomePage(
    ): ListPaintingResponse

    @GET("genreList")
    suspend fun genreList(): ListPaintingResponse

    @FormUrlEncoded
    @POST("user")
    suspend fun userProfile(
        @Field("email") email: String
    ): LoginResponse

    @FormUrlEncoded
    @POST("userPaintings")
    suspend fun userPaintings(
        @Field("email") email: String
    ): ListPaintingResponse
}