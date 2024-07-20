package com.example.jarvisv2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.jarvisv2.R
import com.example.jarvisv2.adapter.ChatAdapter
import com.example.jarvisv2.models.Chat
import com.example.jarvisv2.utils.Status
import com.example.jarvisv2.utils.copyToClipBoard
import com.example.jarvisv2.utils.hideKeyBoard
import com.example.jarvisv2.utils.longToastShow
import com.example.jarvisv2.utils.shareMsg
import com.example.jarvisv2.view_models.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date

class ChatScreenFragment : Fragment() {



    private val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_screen, container, false)

        val chat_rv = view.findViewById<RecyclerView>(R.id.chat_rv)
        val chat_adapter = ChatAdapter() { message, text_view ->

            val popup = PopupMenu(context, text_view)
            try {
                val fields = popup.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field.get(popup)
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            popup.menuInflater.inflate(R.menu.option_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.copy_menu -> {
                        view.context.copyToClipBoard(message)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.select_txt_menu -> {
                        val action =
                            ChatScreenFragmentDirections.actionChatScreenFragmentToSelectTextScreenFragment(
                                message
                            )
                        findNavController().navigate(action)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.share_txt_menu -> {
                        view.context.shareMsg(message)
                        return@setOnMenuItemClickListener true
                    }

                    else -> {
                        return@setOnMenuItemClickListener true
                    }
                }
            }
            popup.show()

        }
        chat_rv.adapter = chat_adapter
        chat_adapter.registerAdapterDataObserver(object:RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                chat_rv.smoothScrollToPosition(positionStart)
            }
        })
        val send_image_btn = view.findViewById<ImageButton>(R.id.send_image_btn)
        val ed_message = view.findViewById<EditText>(R.id.ed_message)

        send_image_btn.setOnClickListener {
            view.context.hideKeyBoard(it)
            if (ed_message.text.toString().trim().isNotEmpty()) {
                chatViewModel.createChatCompletion(ed_message.text.toString().trim())
                ed_message.text = null
            } else {
                view.context.longToastShow("Message is required")
            }
        }

        callGetChatList(chat_rv,chat_adapter)
        chatViewModel.getChatList()



        return view
    }

    private fun callGetChatList(chatRv: RecyclerView, chatAdapter: ChatAdapter) {
        CoroutineScope(Dispatchers.Main).launch {
            chatViewModel
                .chatStateFlow
                .collectLatest {
                    when(it.status){
                        Status.LOADING ->{}
                        Status.SUCCESS ->{
                            it.data?.collect{chatList->
                                chatAdapter.submitList(chatList)
                            }
                        }
                        Status.ERROR ->{
                            it.message?.let { it1 -> chatRv.context.longToastShow(it1) }
                        }
                    }
                }
        }
    }
}