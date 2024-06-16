package com.example.artnaon.data.pref

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun saveMessages(context: Context, userId: String, messages: List<MessageModel>) {
    val sharedPreferences = context.getSharedPreferences("chat_prefs_$userId", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    val gson = Gson()
    val json = gson.toJson(messages)
    editor.putString("chat_messages", json)
    editor.apply()
}

fun loadMessages(context: Context, userId: String): List<MessageModel> {
    val sharedPreferences = context.getSharedPreferences("chat_prefs_$userId", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPreferences.getString("chat_messages", null)
    val type = object : TypeToken<List<MessageModel>>() {}.type
    return if (json != null) {
        gson.fromJson(json, type)
    } else {
        emptyList()
    }
}