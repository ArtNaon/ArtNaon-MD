package com.example.artnaon.ui.view.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.artnaon.data.repository.UserRepository
import com.example.artnaon.data.response.Result
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository): ViewModel() {

    private val _logoutState = MutableLiveData<Boolean>()
    val logoutState: LiveData<Boolean> get() = _logoutState

    val themeSetting: LiveData<Boolean> = repository.getThemeSetting().asLiveData()

    private val _paintings = MutableLiveData<List<String>>()
    val paintings: LiveData<List<String>> = _paintings

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _logoutState.value = true
        }
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            repository.saveThemeSetting(isDarkModeActive)
        }
    }

    fun fetchUserPaintings(email: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUserPaintings(email)
                _paintings.value = response.result?.filterNotNull() ?: emptyList()
            } catch (_: Exception) {

            }
        }
    }
}