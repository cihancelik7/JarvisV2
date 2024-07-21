package com.example.jarvisv2.repository

import android.app.Application
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
    val _statusLiveData = LiveData<Resource<StatusResult>>()
        get() = _statusLiveData

    fun insertRobot(robot:Robot){
        try {
            _statusLiveData.postValue(Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = robotDao.insertRobot(robot)
                handleResult(result.toInt(),"Inserted robot successfully",StatusResult.Added)
            }
        }catch (e:Exception){

        }
    }
    private fun handleResult(toInt: Int,s:String,Added:StatusResult){

    }
}