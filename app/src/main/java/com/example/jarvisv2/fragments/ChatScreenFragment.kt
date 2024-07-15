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
import com.example.jarvisv2.utils.copyToClipBoard
import com.example.jarvisv2.utils.hideKeyBoard
import com.example.jarvisv2.utils.longToastShow
import com.example.jarvisv2.utils.shareMsg
import com.example.jarvisv2.view_models.ChatViewModel
import java.util.Date

class ChatScreenFragment : Fragment() {

    private val chatList = arrayListOf(
        Chat(
            "1",
            "Hi, how can I integrate Retrofit in Android development?",
            "sender",
            Date()
        ),
        Chat(
            "2",
            "You can integrate Retrofit by adding it as a dependency in your app's build.gradle file.",
            "receiver",
            Date()
        ),
        Chat(
            "3",
            "What is the purpose of ViewModel in MVVM architecture?",
            "sender",
            Date()
        ),
        Chat(
            "4",
            "ViewModel is used to store and manage UI-related data, separating it from the UI components.",
            "receiver",
            Date()
        ),
        Chat(
            "5",
            "What is the purpose of ViewModel in MVVM architecture?",
            "sender",
            Date()
        ),
        Chat(
            "6",
            "ViewModel is used to store and manage UI-related data, separating it from the UI components.",
            "receiver",
            Date()
        ),
        Chat(
            "7",
            "How do I implement a RecyclerView in Android?",
            "sender",
            Date()
        ),
        Chat(
            "8",
            "You can create a RecyclerView by defining a layout, adapter, and connecting it to your data source.",
            "receiver",
            Date()
        ),
        Chat(
            "9",
            "Can you suggest a good Android IDE?",
            "sender",
            Date()
        ),
        Chat(
            "10",
            "Android Studio is the official IDE for Android app development and is highly recommended.",
            "receiver",
            Date()
        ),
        Chat(
            "11",
            "How can I implement a swipe-to-refresh feature in my Android app?",
            "sender",
            Date()
        ),
        Chat(
            "12",
            "You can implement swipe-to-refresh by using the SwipeRefreshLayout widget in your layout XML and handling refresh events in your code.",
            "receiver",
            Date()
        )
    )

    private val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this)[ChatViewModel::class.java]
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

        val send_image_btn = view.findViewById<ImageButton>(R.id.send_image_btn)
        val ed_message = view.findViewById<EditText>(R.id.ed_message)
   //     var counter = -1
        send_image_btn.setOnClickListener {
            view.context.hideKeyBoard(it)
            if (ed_message.text.toString().trim().isNotEmpty()) {
             /*   counter += 1
                if (counter >= chatList.size) {
                    return@setOnClickListener
                }
                chatViewModel.insertChat(chatList[counter])*/
                chatViewModel.createChatCompletion(ed_message.text.toString().trim())
            } else {
                view.context.longToastShow("Message is required")
            }
        }

        chatViewModel.chatList.observe(viewLifecycleOwner) {
            chat_adapter.submitList(it)
            chat_rv.smoothScrollToPosition(it.size)
        }

        return view
    }


}