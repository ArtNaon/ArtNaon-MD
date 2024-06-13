package com.example.artnaon.ui.view.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.artnaon.data.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository): ViewModel() {

    private val _logoutState = MutableLiveData<Boolean>()
    val logoutState: LiveData<Boolean> get() = _logoutState

    val themeSetting: LiveData<Boolean> = repository.getThemeSetting().asLiveData()

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
}