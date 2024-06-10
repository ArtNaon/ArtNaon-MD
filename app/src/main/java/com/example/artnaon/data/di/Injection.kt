package com.example.artnaon.data.di

import android.content.Context
import com.example.artnaon.data.api.ApiConfig
import com.example.artnaon.data.pref.UserPreference
import com.example.artnaon.data.pref.dataStore
import com.example.artnaon.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val preferences = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(preferences, apiService)
    }
}