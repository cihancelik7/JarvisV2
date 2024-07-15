package com.example.jarvisv2.response

data class ChatRequest(
    val messages: List<Message>,
    val model: String
)