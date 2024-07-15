package com.example.jarvisv2.network

import com.example.jarvisv2.response.ChatRequest
import com.example.jarvisv2.response.ChatResponse
import com.example.jarvisv2.utils.OPENAI_API_KEY
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @POST("chat/completions")
    fun create_chat_completion(
        @Body chat_request: ChatRequest,
        @Header("Content-Type") contentType: String = "application/json",
        @Header("Authorization") authorization: String = "Bearer $OPENAI_API_KEY",
    ): Call<ChatResponse>
}
