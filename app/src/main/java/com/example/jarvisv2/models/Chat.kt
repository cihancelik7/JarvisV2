package com.example.jarvisv2.models

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.jarvisv2.response.Message
import java.util.Date


@Entity(
    foreignKeys = [ForeignKey(
        entity = Robot::class,
        parentColumns = ["robotId"],
        childColumns = ["robotId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Chat(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "chatId")
    val chatId: String,
    @Embedded
    val message: Message,
    val robotId: String,
    val date: Date
)
