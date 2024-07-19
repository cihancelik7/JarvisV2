package com.example.jarvisv2.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.jarvisv2.models.Chat
import com.example.jarvisv2.repository.ChatRepository

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val chat_repository = ChatRepository()

    var chatList = MutableLiveData<List<Chat>>(arrayListOf())
        private set

    fun insertChat(chat: Chat) {
        val modifiedChatList = ArrayList<Chat>().apply {
            chatList.value?.let { addAll(it) }
        }
        modifiedChatList.add(chat)
        chatList.postValue(modifiedChatList)
    }

    fun createChatCompletion(message: String) {
        chat_repository.create_chat_complation(message)
    }
}