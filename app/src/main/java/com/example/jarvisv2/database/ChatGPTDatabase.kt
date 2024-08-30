package com.example.jarvisv2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jarvisv2.converters.TypeConverter
import com.example.jarvisv2.dao.ChatDao
import com.example.jarvisv2.dao.RobotDao
import com.example.jarvisv2.dao.CategoryDao  // Yeni eklenen DAO
import com.example.jarvisv2.models.Chat
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.models.ChatCategory  // Yeni eklenen model
import com.example.jarvisv2.models.ImageCategory // Yeni eklenen model

@Database(
    entities = [Chat::class, Robot::class, ChatCategory::class, ImageCategory::class], // Yeni eklenen modeller
    version = 2, // Versiyon numarasını artırdık
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class ChatGPTDatabase : RoomDatabase() {
    abstract val chatDao: ChatDao
    abstract val robotDao: RobotDao
    abstract val categoryDao: CategoryDao // Yeni DAO

    companion object {

        @Volatile
        private var INSTANCE: ChatGPTDatabase? = null

        fun getInstance(context: Context): ChatGPTDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ChatGPTDatabase::class.java,
                    "chat_gpt_db"
                )
                    .fallbackToDestructiveMigration() // Versiyon uyumsuzluklarını ele almak için
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }
}