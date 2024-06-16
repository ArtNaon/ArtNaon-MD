package com.example.artnaon.data.repository

import android.util.Log
import com.example.artnaon.data.api.ApiService
import com.example.artnaon.data.pref.UserModel
import com.example.artnaon.data.pref.UserPreference
import com.example.artnaon.data.response.ListPaintingResponse
import com.example.artnaon.data.response.EditProfileResponse
import com.example.artnaon.data.response.LoginResponse
import com.example.artnaon.data.response.RegisterResponse
import com.example.artnaon.data.response.ResetPasswordResponse
import com.example.artnaon.data.response.UserResult
import com.example.artnaon.ui.view.chatbot.GeminiAI
import com.google.ai.client.generativeai.java.ChatFutures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException

class UserRepository(
    private val preference: UserPreference,
    private val apiService: ApiService
) {

    suspend fun userSignUp(name: String, email: String, password: String): RegisterResponse {
        return try {
            apiService.register(name, email, password)
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()
            throw Exception(body)
        } catch (e: Throwable) {
            throw Exception(e.message)
        }
    }

    suspend fun editProfile(name: String, password: String, picture: MultipartBody.Part?): EditProfileResponse {
        val email = preference.getSession().first().email
        val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = apiService.editProfile(emailBody, nameBody, passwordBody, picture)

        val updatedUser = UserModel(
            name = response.result?.nameEditProfile ?: "",
            email = email,
            token = preference.getSession().first().token,
            isLogin = true,
            picture = response.result?.pictureEditProfile // Simpan profilePicture
        )
        preference.saveSession(updatedUser)

        return response
    }

    suspend fun userSignIn(email: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(email, password)
            val token = response.result?.token ?: ""
            val model = UserModel(name = "", email = email, token = token, isLogin = true)
            saveSession(model)
            response
        } catch (e: HttpException) {
            val errorMessage = try {
                val body = e.response()?.errorBody()?.string()
                if (body != null) {
                    JSONObject(body).getString("message")
                } else {
                    "Unknown error occurred."
                }
            } catch (jsonException: JSONException) {
                Log.e("SignIn Error", "Error parsing error body: $e")
                "Something went wrong. Please try again."
            }
            throw Exception(errorMessage)
        } catch (e: Throwable) {
            throw Exception(e.message ?: "Unknown error")
        }
    }

    suspend fun resetPassword(email: String, newPassword: String): ResetPasswordResponse {
        return try {
            apiService.resetPassword(email, newPassword)
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()
            val errorMessage = try {
                if (body != null) {
                    JSONObject(body).getString("message")
                } else {
                    "Unknown error occurred."
                }
            } catch (jsonException: JSONException) {
                "Something went wrong. Please try again."
            }
            throw Exception(errorMessage)
        } catch (e: Throwable) {
            throw Exception(e.message ?: "Unknown error")
        }
    }

    suspend fun saveSession(user: UserModel) {
        preference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return preference.getSession()
    }

    suspend fun logout() {
        preference.logout()
    }

    fun getThemeSetting(): Flow<Boolean> {
        return preference.getThemeSetting()
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        preference.saveThemeSetting(isDarkModeActive)
    }

    suspend fun getUserDetails(email: String): UserResult? {
        return try {
            val response = apiService.userProfile(email)
            response.result
        } catch (e: HttpException) {
            val body = e.response()?.errorBody()?.string()
            throw Exception(body)
        } catch (e: Throwable) {
            throw Exception(e.message)
        }
    }

    suspend fun deletePainting(paintingUrl: String): ListPaintingResponse {
        val requestBody = mapOf("imageUrl" to paintingUrl)
        return apiService.deletePainting(requestBody)
    }

    fun getChatFutures(): ChatFutures {
        return GeminiAI.getModelGemini().startChat()
    }

    fun sendMessageToGemini(chatFutures: ChatFutures, message: String, callback: (Result<String>) -> Unit) {
        GeminiAI.getResponse(chatFutures, message, callback)
    }

    companion object {
        fun getInstance(
            preferences: UserPreference,
            apiService: ApiService
        ) = UserRepository(preferences, apiService)
    }
}
