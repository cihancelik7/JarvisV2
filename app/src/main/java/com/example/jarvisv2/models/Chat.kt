package com.example.jarvisv2.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jarvisv2.response.Message
import java.util.Date


@Entity()
data class Chat(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "chatId")
    val chatId: String,
    @Embedded
    val message: Message,
    val date: Date
)
