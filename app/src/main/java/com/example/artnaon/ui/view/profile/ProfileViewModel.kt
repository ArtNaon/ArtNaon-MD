package com.example.artnaon.ui.view.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.artnaon.data.repository.UserRepository
import com.example.artnaon.data.response.EditProfileResponse
import com.example.artnaon.data.response.ListPaintingResponse
import com.example.artnaon.data.response.Result
import com.example.artnaon.data.response.UserResult
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ProfileViewModel(private val repository: UserRepository): ViewModel() {

    private val _logoutState = MutableLiveData<Boolean>()

    private val _editProfileResult = MutableLiveData<EditProfileResponse>()
    val editProfileResult: LiveData<EditProfileResponse> get() = _editProfileResult

    private val _userDetails = MutableLiveData<UserResult>()
    val userDetails: LiveData<UserResult> get() = _userDetails

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

    fun fetchUserDetails(email: String) {
        viewModelScope.launch {
            try {
                val response = repository.getUserDetails(email)
                Log.d("ProfileViewModel", "User details fetched: $response")
                _userDetails.value = response!!
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error fetching user details", e)
            }
        }
    }

    fun editProfile(name: String, password: String, picture: MultipartBody.Part?) {
        viewModelScope.launch {
            try {
                val response = repository.editProfile(name, password, picture)
                _editProfileResult.value = response
            } catch (e: Exception) {
                // Handle error
            }

            fun fetchUserPaintings(email: String) {
                viewModelScope.launch {
                    try {
                        val response = repository.getUserDetails(email)
                        _userDetails.value = response!!
                    } catch (e: Exception) {
                        // handle error
                    }
                }
            }
        }
    }

    suspend fun deletePainting(paintingUrl: String): ListPaintingResponse {
        return repository.deletePainting(paintingUrl)
    }
}