package com.example.jarvisv2.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jarvisv2.models.ChatCategory
import com.example.jarvisv2.models.ImageCategory

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatCategory(category: ChatCategory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImageCategory(category: ImageCategory): Long

    @Query("SELECT * FROM ChatCategory WHERE email == :email")
    suspend fun getChatCategoryByEmail(email: String): List<ChatCategory>

    @Query("SELECT * FROM ImageCategory WHERE email == :email")
    suspend fun getImageCategoryByEmail(email: String): List<ImageCategory>
}