package com.example.jarvisv2.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.repository.RobotRepository

class RobotViewModel(application: Application) : AndroidViewModel(application) {

    private val robotRepository = RobotRepository(application)
    val robotStateFlow get() = robotRepository.robotStateFlow
    val statusLiveData get() = robotRepository.statusLiveData

    // Firebase'e robot ekleme işlemi
    fun insertRobot(robot: Robot, userEmail: String) {
        robotRepository.insertRobot(robot, )
    }

    // Firebase'den robotları getirme işlemi
    fun getRobotList(userEmail: String) {
        robotRepository.getRobotList()
    }

    // Firebase'de robot güncelleme işlemi
    fun updateRobot(robot: Robot, userEmail: String) {
        robotRepository.updateRobot(robot)
    }

    // Firebase'de robot silme işlemi
    fun deleteRobotUsingId(robotId: String, userEmail: String) {
        robotRepository.deleteRobotUsingId(robotId)
    }

    fun clearStatusLiveData() {
        robotRepository.clearStatusLiveData()
    }
}