package com.example.artnaon.ui.view.chatbot

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.artnaon.data.repository.UserRepository
import com.google.ai.client.generativeai.java.ChatFutures

class GeminiViewModel(private val repository: UserRepository) : ViewModel() {

    private val _response = MutableLiveData<String>()
    val response: LiveData<String> = _response

    private val chatFutures: ChatFutures = repository.getChatFutures()

    fun sendMessage(message: String) {
        repository.sendMessageToGemini(chatFutures, message) { result ->
            result.onSuccess {
                _response.postValue(it)
            }.onFailure {
                _response.postValue("Sorry, I'm having trouble understanding that. Please try again.")
            }
        }
    }
}
