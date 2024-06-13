package com.example.artnaon.ui.view.signin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.artnaon.data.pref.UserModel
import com.example.artnaon.data.repository.UserRepository
import com.example.artnaon.data.response.LoginResponse
import kotlinx.coroutines.launch

class SignInViewModel(private val repository: UserRepository) : ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    suspend fun userSignIn(email: String, password: String): Result<LoginResponse> {
        if (email.isEmpty() || password.isEmpty()) {
            _errorMessage.postValue("Fields cannot be empty")
            return Result.failure(Exception("Fields cannot be empty"))
        }

        if (password.length < 8) {
            _errorMessage.postValue("Password must be at least 8 characters")
            return Result.failure(Exception("Password must be at least 8 characters"))
        }

        return try {
            val response = repository.userSignIn(email, password)
            Result.success(response)
        } catch (e: Exception) {
            _errorMessage.postValue(e.message ?: "Unknown error")
            return Result.failure(e)
        }
    }

    fun saveSession(model: UserModel) {
        viewModelScope.launch {
            repository.saveSession(model)
        }
    }

}