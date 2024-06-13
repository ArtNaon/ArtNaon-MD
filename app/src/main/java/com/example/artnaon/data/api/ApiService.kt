package com.example.artnaon.data.api

import com.example.artnaon.data.response.ListPaintingResponse
import com.example.artnaon.data.response.LoginResponse
import com.example.artnaon.data.response.RegisterResponse
import com.example.artnaon.data.response.ResetPasswordResponse
import com.example.artnaon.data.response.UploadResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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
    suspend fun getHomePage(): ListPaintingResponse

    @Multipart
    @POST("upload")
    suspend fun uploadPainting(
        @Part("genre") genre: RequestBody,
        @Part("description") description: RequestBody,
        @Part painting: MultipartBody.Part
    ): UploadResponse
}


