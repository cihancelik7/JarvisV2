package com.example.jarvisv2.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChatCategory(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val email : String,
    val categoryName: String
)