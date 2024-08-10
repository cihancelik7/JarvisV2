package com.example.jarvisv2.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jarvisv2.models.Chat
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface ChatDao {

    @Query("SELECT * FROM Chat WHERE robotId == :robotId ORDER BY date DESC")
    fun getChatList(robotId:String): Flow<List<Chat>>

    @Query("SELECT * FROM Chat WHERE robotId == :robotId ORDER BY date DESC LIMIT 5")
    fun getChatListWithOutFlow(robotId:String): List<Chat>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: Chat): Long

    @Query("DELETE FROM Chat WHERE chatId == :chatId")
    suspend fun deleteChatUsingChatId(chatId: String): Int

    @Query("UPDATE Chat SET content=:content, role=:role, date=:date WHERE chatId == :chatId")
    suspend fun updateChatParticularField(
        chatId: String,
        content: String,
        role: String,
        date: Date
    )
}