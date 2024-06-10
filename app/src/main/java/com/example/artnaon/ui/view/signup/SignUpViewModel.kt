package com.example.artnaon.ui.view.signup

import androidx.lifecycle.ViewModel
import com.example.artnaon.data.repository.UserRepository
import com.example.artnaon.data.response.RegisterResponse

class SignUpViewModel(private val repository: UserRepository) : ViewModel() {
    suspend fun userSignUp(name: String, email: String, password: String): RegisterResponse {
        return repository.userSignUp(name, email, password)
    }
}