package com.example.jarvisv2.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.jarvisv2.MainActivity
import com.example.jarvisv2.R
import com.example.jarvisv2.adapter.ChatAdapter
import com.example.jarvisv2.utils.Status
import com.example.jarvisv2.utils.copyToClipBoard
import com.example.jarvisv2.utils.hideKeyBoard
import com.example.jarvisv2.utils.longToastShow
import com.example.jarvisv2.utils.robotImageList
import com.example.jarvisv2.utils.shareMsg
import com.example.jarvisv2.view_models.ChatViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class ChatScreenFragment : Fragment() {

    private val chatViewModel: ChatViewModel by lazy {
        ViewModelProvider(this).get(ChatViewModel::class.java)
    }
    private lateinit var textToSpeech: TextToSpeech

    private val chatArgs: ChatScreenFragmentArgs by navArgs()
    private lateinit var edMessage: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat_screen, container, false)

        val toolBarView = view.findViewById<View>(R.id.toolbarLayout)

        toolBarView?.let {
            val closeImage = it.findViewById<ImageView>(R.id.back_img)
            val robotImage = it.findViewById<ImageView>(R.id.robot_image)
            robotImage.setImageResource(robotImageList[chatArgs.robotimage])
            closeImage.setOnClickListener {
                findNavController().navigateUp()
            }

            val titleTxt = it.findViewById<TextView>(R.id.titleTxt)
            titleTxt.text = chatArgs.robotName
        } ?: run {
            // Eğer toolbar bulunamazsa bir hata kaydedilir.
            Log.e("ChatScreenFragment", "Toolbar view bulunamadı!")
        }

        textToSpeech = TextToSpeech(view.context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED
                ) {
                    view.context.longToastShow("language is not supported")
                }
            }
        }

        val chatRv = view.findViewById<RecyclerView>(R.id.chat_rv)
        val chatAdapter = ChatAdapter { message, textView ->

            val popup = PopupMenu(context, textView)
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
                        textToSpeech.stop()
                        view.context.copyToClipBoard(message)
                        return@setOnMenuItemClickListener true
                    }

                    R.id.select_txt_menu -> {
                        textToSpeech.stop()
                        val action =
                            ChatScreenFragmentDirections.actionChatScreenFragmentToSelectTextScreenFragment(
                                message
                            )
                        findNavController().navigate(action)
                        return@setOnMenuItemClickListener true
                    }
                    R.id.textToVoiceMenu -> {
                        textToSpeech.speak(
                            message,
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            null
                        )
                        return@setOnMenuItemClickListener true
                    }

                    R.id.share_txt_menu -> {
                        textToSpeech.stop()
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
        chatRv.adapter = chatAdapter
        chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                chatRv.smoothScrollToPosition(positionStart)
            }
        })
        val sendImageBtn = view.findViewById<ImageButton>(R.id.send_image_btn)
        edMessage = view.findViewById(R.id.ed_message)

        sendImageBtn.setOnClickListener {
            textToSpeech.stop()
            view.context.hideKeyBoard(it)
            if (edMessage.text.toString().trim().isNotEmpty()) {
                chatViewModel.createChatCompletion(edMessage.text.toString().trim(), chatArgs.robotId)
                edMessage.text = null
            } else {
                view.context.longToastShow("Message is required")
            }
        }
        val voiceToTextBtn = view.findViewById<ImageButton>(R.id.voiceToTextBtn)
        voiceToTextBtn.setOnClickListener {
            textToSpeech.stop()
            edMessage.text = null
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something")
                result.launch(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        callGetChatList(chatRv, chatAdapter)
        chatViewModel.getChatList(chatArgs.robotId)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textToSpeech.stop()
    }

    private val result = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.getStringArrayListExtra(
                RecognizerIntent.EXTRA_RESULTS
            ) as ArrayList<String>
            edMessage.setText(results[0])
        }
    }

    private fun callGetChatList(chatRv: RecyclerView, chatAdapter: ChatAdapter) {
        CoroutineScope(Dispatchers.Main).launch {
            chatViewModel
                .chatStateFlow
                .collectLatest {
                    when (it.status) {
                        Status.LOADING -> {}
                        Status.SUCCESS -> {
                            it.data?.collect { chatList ->
                                chatAdapter.submitList(chatList)
                            }
                        }
                        Status.ERROR -> {
                            it.message?.let { it1 -> chatRv.context.longToastShow(it1) }
                        }
                    }
                }
        }
    }
}