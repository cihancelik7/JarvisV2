package com.example.jarvisv2.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jarvisv2.database.ChatGPTDatabase
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.utils.Resource
import com.example.jarvisv2.utils.StatusResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RobotRepository(application: Application) {
    private val robotDao = ChatGPTDatabase.getInstance(application).robotDao


    private val _statusLiveData = MutableLiveData<Resource<StatusResult>>()
    val statusLiveData: LiveData<Resource<StatusResult>>
        get() = _statusLiveData


    fun insertRobot(robot: Robot) {
        try {
            _statusLiveData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = robotDao.insertRobot(robot)
                handleResult(result.toInt(), "Inserted robot successfully", StatusResult.Added)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Resource.Error(e.message.toString()))
        }
    }

    private fun handleResult(result: Int, message: String, statusResult: StatusResult) {
        if (result!=-1){
            _statusLiveData.postValue(Resource.Success(statusResult,message))
        }else{
            _statusLiveData.postValue(Resource.Error("Somethıng went wrong"))
        }
    }
}