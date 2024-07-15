package com.example.jarvisv2.repository

import android.util.Log
import com.example.jarvisv2.network.ApiClient
import com.example.jarvisv2.response.ChatRequest
import com.example.jarvisv2.response.ChatResponse
import com.example.jarvisv2.response.Message
import com.example.jarvisv2.utils.CHATGPT_MODEL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRepository {
    private val api_client = ApiClient.getInstance()

    fun create_chat_complation(message: String) {
        try {
            val chatRequest = ChatRequest(
                arrayListOf(
                    Message(
                        "Hey there, I'm Jarvis, designed by Cihan. Here to turn complex problems into simple solutions. What's the topic of the day?",
                        "system"
                    ),
                    Message(
                        message,
                        "user"
                    )
                ),
                CHATGPT_MODEL
            )
            api_client.create_chat_completion(chatRequest).enqueue(object : Callback<ChatResponse> {
                override fun onResponse(
                    call: Call<ChatResponse>,
                    response: Response<ChatResponse>
                ) {
                    val code = response.code()
                    if (code == 200) {
                        response.body()?.choices?.get(0)?.message?.let {
                            Log.d("message", it.toString())
                        }
                    } else {
                        Log.d("error", response.errorBody().toString())
                    }
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    t.printStackTrace()
                }

            })

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
