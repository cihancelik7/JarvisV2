package com.example.jarvisv2.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.jarvisv2.models.Chat

class ChatViewModel(application: Application) :AndroidViewModel(application){
    var chatList = MutableLiveData<List<Chat>>(arrayListOf())

    fun insertChat(chat:Chat){
        val modifiedChatList = ArrayList<Chat>().apply {
            addAll(chatList.value!!)
        }
        modifiedChatList.add(chat)
        chatList.postValue(modifiedChatList)
    }
}