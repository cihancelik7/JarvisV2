package com.example.jarvisv2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.jarvisv2.databinding.ItemReceiverBinding
import com.example.jarvisv2.databinding.ItemSenderBinding
import com.example.jarvisv2.model.Chat
import java.text.SimpleDateFormat
import java.util.Locale

class ChatAdapter : ListAdapter<Chat, RecyclerView.ViewHolder>(DiffCallback()) {


    class SenderViewHolder(private val itemSenderBinding: ItemSenderBinding) :
        RecyclerView.ViewHolder(itemSenderBinding.root) {
        fun bind(chat: Chat) {
            itemSenderBinding.txtMessage.text = chat.message
            val data_format = SimpleDateFormat("dd-MMM-yyyy HH:mm a", Locale.getDefault())
            itemSenderBinding.txtDate.text = data_format.format(chat.date)
        }
    }

    class ReceiverViewHolder(private val itemReceiverBinding: ItemReceiverBinding) :
        RecyclerView.ViewHolder(itemReceiverBinding.root) {
        fun bind(chat: Chat) {
            itemReceiverBinding.txtMessage.text = chat.message
            val data_format = SimpleDateFormat("dd-MMM-yyyy HH:mm a", Locale.getDefault())
            itemReceiverBinding.txtDate.text = data_format.format(chat.date)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) { // Receiver_item
            ReceiverViewHolder(
                ItemReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }else{
            SenderViewHolder( // Sender_item
                ItemSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val chat = getItem(position)

        if (chat.message_type.equals("sender",true)){
            (holder as SenderViewHolder).bind(chat)
        }else{
            (holder as ReceiverViewHolder).bind(chat)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).message_type.equals("sender",true)){
            0 //Sender_item
        }else{
            1 // Receiver_item
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Chat>() {
        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }

    }

}