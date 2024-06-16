package com.example.artnaon.ui.view.chatbot

import com.example.artnaon.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.ChatFutures
import com.google.ai.client.generativeai.java.GenerativeModelFutures
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.GenerationConfig
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.Executor

class GeminiAI {
    companion object {

        private const val PROMPT_PREFIX = "Kamu adalah seorang ArtNaon Bot dan merupakan ahli seni. Jawablah hanya terkait dengan lukisan dan genre lukisan." +
                "The world is full of art galleries showcasing various forms of art. However, not everyone is able to comprehend the meaning and genre of the art they are viewing, especially if it is too complex to understand. This can be quite disheartening for people who wish to appreciate art in its true form. This is where ArtNaon comes in to provide a solution to this problem. It simplifies the process of identifying art and provides an explanation of the art genre to make it easier for everyone to understand. In addition to this, ArtNaon also aims to help people appreciate art for its emotions and feelings, not just for its aesthetic value. It encourages people to delve deeper into the art to understand the artist's thought process behind the creation. This helps people to connect with the art and appreciate it from a different perspective." +
                "ArtNaon merupakan aplikasi untuk klasifikasi genre lukisan dan terdiri dari Abstract, Expressionism, Neoclassicism, Primitivism, Realism, Romanticism, and Symbolism"

        fun getResponse(chatFutures: ChatFutures, message: String, callback: (Result<String>) -> Unit) {

            val userMessage = Content.Builder().apply {
                role = "user"
                text("$PROMPT_PREFIX$message")
            }.build()

            val executor = Executor { it.run() }

            val response: ListenableFuture<GenerateContentResponse> = chatFutures.sendMessage(userMessage)

            Futures.addCallback(response, object : FutureCallback<GenerateContentResponse> {
                override fun onSuccess(result: GenerateContentResponse?) {
                    result?.text?.let { callback(Result.success(it)) }
                }

                override fun onFailure(t: Throwable) {
                    t.printStackTrace()
                    callback(Result.failure(t))
                }
            }, executor)
        }

        fun getModelGemini(): GenerativeModelFutures {
            val apiKey = BuildConfig.GEMINI_API_KEY

            val harassmentSafety = SafetySetting(
                HarmCategory.HARASSMENT,
                BlockThreshold.ONLY_HIGH
            )

            val generationConfig = GenerationConfig.Builder().apply {
                temperature = 0.9f
                topK = 16
                topP = 0.1f
            }.build()

            val model = GenerativeModel(
                "gemini-1.5-flash",
                apiKey,
                generationConfig,
                listOf(harassmentSafety)
            )

            return GenerativeModelFutures.from(model)
        }
    }
}
