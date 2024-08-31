package com.example.jarvisv2.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.example.jarvisv2.database.ChatGPTDatabase
import com.example.jarvisv2.models.Chat
import com.example.jarvisv2.network.ApiClient
import com.example.jarvisv2.response.ChatRequest
import com.example.jarvisv2.response.ChatResponse
import com.example.jarvisv2.response.CreateImageRequest
import com.example.jarvisv2.response.Data
import com.example.jarvisv2.response.ImageResponse
import com.example.jarvisv2.response.Message
import com.example.jarvisv2.utils.CHATGPT_MODEL
import com.example.jarvisv2.utils.EncryptSharedPreferenceManager
import com.example.jarvisv2.utils.Resource
import com.example.jarvisv2.utils.longToastShow
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList
import java.util.Date
import java.util.UUID
import kotlin.math.E

class ChatRepository(val application: Application) {

    private val chatDao = ChatGPTDatabase.getInstance(application).chatDao
    private val firebaseRepository = FirebaseRepository()
    private val databaseReference = FirebaseDatabase.getInstance().reference  // Firebase bağlantısı


    private val api_client = ApiClient.getInstance()
    private val encryptSharedPreferenceManager = EncryptSharedPreferenceManager(application)

    private val _chatStateFlow = MutableStateFlow<Resource<Flow<List<Chat>>>>(Resource.Loading())
    val chatStateFlow: StateFlow<Resource<Flow<List<Chat>>>>
        get() = _chatStateFlow

    private val _imageStateFlow = MutableStateFlow<Resource<ImageResponse>>(Resource.Success())
    val imageStateFlow: StateFlow<Resource<ImageResponse>>
        get() = _imageStateFlow


    private val imageList = ArrayList<Data>()
    fun createUserSpecificChat(email: String, robotName: String, message: String) {
        //firebaseRepository.addRobotChat(email, robotName, message, "user")
    }

    fun getUserRobotChats(email: String, robotName: String): DatabaseReference {
        return firebaseRepository.getUserRobotChats(email)
    }

    fun createUserSpecificCategory(email: String) {
        firebaseRepository.createUserSpecificCategory()
    }

    fun getChatList(robotId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _chatStateFlow.emit(Resource.Loading())
                val result = async {
                    delay(300)
                    chatDao.getChatList(robotId)
                }.await()

                _chatStateFlow.emit(Resource.Success(result))
            } catch (e: Exception) {
                _chatStateFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }


    fun create_chat_complation(message: String, robotId: String) {
        val receiverId = UUID.randomUUID().toString()
        CoroutineScope(Dispatchers.IO).launch {
            delay(200)
            val senderId = UUID.randomUUID().toString()
            try {
                async {
                    chatDao.insertChat(
                        Chat(
                            senderId,
                            Message(
                                message,
                                "user"
                            ),
                            robotId,
                            Date()
                        )
                    )
                }.await()

                val messageList = chatDao.getChatListWithOutFlow(robotId).map {
                    it.message
                }.reversed().toMutableList()

                if (messageList.size == 1) {
                    messageList.add(
                        0,
                        Message(
                            "Hey there, I'm Jarvis, designed by Cihan. Here to turn complex problems into simple solutions. What's the topic of the day?",
                            "system"
                        )
                    )
                }
                async {
                    chatDao.insertChat(
                        Chat(
                            receiverId,
                            Message(

                                "",
                                "assistant"
                            ),
                            robotId,
                            Date()
                        )
                    )
                }.await()


                val chatRequest = ChatRequest(
                    messageList,
                    CHATGPT_MODEL
                )
                api_client.create_chat_completion(
                    chatRequest,
                    authorization = "Bearer ${encryptSharedPreferenceManager.openAPIKey}"
                )
                    .enqueue(object : Callback<ChatResponse> {
                        override fun onResponse(
                            call: Call<ChatResponse>,
                            response: Response<ChatResponse>
                        ) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val code = response.code()
                                if (code == 200) {
                                    response.body()?.choices?.get(0)?.message?.let {
                                        Log.d("message", it.toString())
                                        chatDao.updateChatParticularField(
                                            receiverId,
                                            it.content,
                                            it.role,
                                            Date()
                                        )
                                    }
                                } else {
                                    Log.d("error", response.errorBody().toString())
                                    deleteChatIfApiFailure(receiverId, senderId)
                                }
                            }
                        }

                        override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                            t.printStackTrace()
                            deleteChatIfApiFailure(receiverId, senderId)

                        }

                    })

            } catch (e: Exception) {
                e.printStackTrace()
                deleteChatIfApiFailure(receiverId, senderId)

            }
        }
    }

    private fun deleteChatIfApiFailure(receiverId: String, senderId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            listOf(
                async { chatDao.deleteChatUsingChatId(receiverId) },
                async { chatDao.deleteChatUsingChatId(senderId) }
            ).awaitAll()
            withContext(Dispatchers.Main) {
                application.longToastShow("Something went wrong")
            }
            //      _chatStateFlow.emit(Resource.Error("Something went wrong"))
        }
        }
        fun create_image(body: CreateImageRequest) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    _imageStateFlow.emit(Resource.Loading())
                    api_client.create_image(
                        body,
                        authorization = "Bearer ${encryptSharedPreferenceManager.openAPIKey}"
                    ).enqueue(object : Callback<ImageResponse> {
                        override fun onResponse(
                            call: Call<ImageResponse>,
                            response: Response<ImageResponse>
                        ) {
                            CoroutineScope(Dispatchers.IO).launch {
                                val responseBody = response.body()
                                if (responseBody != null){
                                    imageList.addAll(responseBody.data)
                                    val modifiedDataList = ArrayList<Data>().apply {
                                        addAll(imageList)
                                    }
                                    val imageResponse = ImageResponse(
                                        responseBody.created,
                                        modifiedDataList
                                    )
                                    _imageStateFlow.emit(Resource.Success(imageResponse))
                                }else{
                                    _imageStateFlow.emit(Resource.Success(null))
                                }
                            }
                        }

                        override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                            t.printStackTrace()
                            CoroutineScope(Dispatchers.IO).launch {
                                _imageStateFlow.emit(Resource.Error(t.message.toString()))
                            }
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    _imageStateFlow.emit(Resource.Error(e.message.toString()))
                }
            }
        }
    }

