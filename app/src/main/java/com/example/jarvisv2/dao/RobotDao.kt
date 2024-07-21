package com.example.jarvisv2.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.jarvisv2.models.Robot

@Dao
interface RobotDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRobot(robot: Robot):Long

}