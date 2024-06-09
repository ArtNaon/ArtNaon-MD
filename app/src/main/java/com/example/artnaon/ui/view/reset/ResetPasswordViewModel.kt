package com.example.artnaon.ui.view.reset

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artnaon.data.repository.UserRepository
import com.example.artnaon.data.response.ResetPasswordResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ResetPasswordViewModel(private val repository: UserRepository) : ViewModel() {

    val resetPasswordResponse = MutableLiveData<ResetPasswordResponse>()
    val errorResponse = MutableLiveData<String>()

    fun resetPassword(email: String, newPassword: String) {
        viewModelScope.launch {
            try {
                val response = repository.resetPassword(email, newPassword)
                resetPasswordResponse.postValue(response)
            } catch (e: Exception) {
                errorResponse.postValue(e.message)
            }
        }
    }
}