package com.example.jarvisv2.models

import java.util.Date

data class Chat(
    val chatId: String,
    val message: String,
    val message_type:String,
    val date: Date
)
