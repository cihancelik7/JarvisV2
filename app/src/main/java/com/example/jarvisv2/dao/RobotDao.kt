package com.example.jarvisv2.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.jarvisv2.models.Robot
import kotlinx.coroutines.flow.Flow


@Dao
interface RobotDao {
    @Query("SELECT * FROM Robot")
    fun getRobotList() :Flow<List<Robot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRobot(robot: Robot):Long

    @Update()
    suspend fun updateRobot(robot: Robot):Int

    @Query("DELETE FROM Robot WHERE robotId = :robotId")
    suspend fun deleteRobotUsingId(robotId:String) : Int


    // Delete all chat messages associated with a specific robotId
    @Query("DELETE FROM Chat WHERE robotId = :robotId")
    suspend fun deleteChatUsingRobotId(robotId:String) : Int


}