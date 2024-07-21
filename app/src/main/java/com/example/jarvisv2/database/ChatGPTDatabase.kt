package com.example.jarvisv2.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jarvisv2.converters.TypeConverter
import com.example.jarvisv2.dao.ChatDao
import com.example.jarvisv2.dao.RobotDao
import com.example.jarvisv2.models.Chat
import com.example.jarvisv2.models.Robot


@Database(
    entities = [Chat::class,Robot::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class ChatGPTDatabase : RoomDatabase(){
    abstract val chatDao:ChatDao
    abstract val robotDao:RobotDao

    companion object{

        @Volatile
        private var INSTANCE: ChatGPTDatabase? = null

        fun getInstance(context: Context): ChatGPTDatabase {
            synchronized(this) {


                  return INSTANCE ?: Room.databaseBuilder(
                        context.applicationContext,
                        ChatGPTDatabase::class.java,
                        "chat_gpt_db"
                    ).build()
                     .also {
                         INSTANCE =it
            }
            }
        }

    }
}