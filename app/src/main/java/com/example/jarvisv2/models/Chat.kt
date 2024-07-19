package com.example.jarvisv2.models

import com.example.jarvisv2.response.Message
import java.util.Date

data class Chat(
    val chatId: String,
    val message: Message,
    val date: Date
)
