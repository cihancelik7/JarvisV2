package com.example.jarvisv2.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.repository.RobotRepository

class RobotViewModel(application: Application):AndroidViewModel(application) {

    private val robotRepository = RobotRepository(application)
    val robotStateFlow get() = robotRepository.robotStateFlow
    val statusLiveData get() = robotRepository.statusLiveData

    fun insertRobot(robot:Robot){
        robotRepository.insertRobot(robot)
    }
    fun clearStatusLiveData(){
        robotRepository.clearStatusLiveData()
    }
    fun getRobotList(){
        robotRepository.getRobotList()
    }
    fun updateRobot(robot:Robot){
        robotRepository.updateRobot(robot)
    }
    fun deleteRobotUsingId(robotId:String){
        robotRepository.deleteRobotUsingId(robotId)
    }
}