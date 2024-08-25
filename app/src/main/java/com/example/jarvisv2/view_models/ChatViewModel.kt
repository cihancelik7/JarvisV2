package com.example.jarvisv2.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.jarvisv2.models.Chat
import com.example.jarvisv2.repository.ChatRepository
import com.example.jarvisv2.response.CreateImageRequest

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chat_repository = ChatRepository(application)
    val chatStateFlow get()=chat_repository.chatStateFlow
        val imageStateFlow get()=chat_repository.imageStateFlow



    fun createChatCompletion(message: String,robotId:String) {
        chat_repository.create_chat_complation(message,robotId)
    }
    fun createImage(body:CreateImageRequest) {
        chat_repository.create_image(body)
    }
    fun getChatList(robotId : String){
        chat_repository.getChatList(robotId)
    }
}