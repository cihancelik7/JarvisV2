package com.example.jarvisv2.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.repository.RobotRepository

class RobotViewModel(application: Application):AndroidViewModel(application) {

    private val robotRepository = RobotRepository(application)
    val statusLiveData get() = robotRepository.statusLiveData

    fun insertRobot(robot:Robot){
        robotRepository.insertRobot(robot)
    }
}