package com.example.artnaon.data.api

import com.example.artnaon.data.response.GenreListResponse
import com.example.artnaon.data.response.HomeGenreResponse
import com.example.artnaon.data.response.ListPaintingResponse
import com.example.artnaon.data.response.LoginResponse
import com.example.artnaon.data.response.RegisterResponse
import com.example.artnaon.data.response.ResetPasswordResponse
import com.example.artnaon.data.response.UploadResponse
import com.example.artnaon.data.response.UserResponse

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

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

    @FormUrlEncoded
    @POST("user")
    suspend fun userProfile(
        @Field("email") email: String
    ): UserResponse

    @FormUrlEncoded
    @POST("userPaintings")
    suspend fun userPaintings(
        @Field("email") email: String
    ): ListPaintingResponse


    @Multipart
    @POST("upload")
    suspend fun uploadPainting(
        @Part("email") email: RequestBody,
        @Part("genre") genre: RequestBody,
        @Part("description") description: RequestBody,
        @Part painting: MultipartBody.Part
    ): UploadResponse

    @GET("genreList")
    suspend fun getGenres(): GenreListResponse

    @POST("genre")
    suspend fun getPaintingsByGenre(@Body request: Map<String, String>): HomeGenreResponse
}
