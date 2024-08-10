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
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RobotRepository(application: Application) {
    private val robotDao = ChatGPTDatabase.getInstance(application).robotDao

    private val _robotStateFlow = MutableStateFlow<Resource<Flow<List<Robot>>>>(Resource.Loading())
    val robotStateFlow: StateFlow<Resource<Flow<List<Robot>>>>
        get() = _robotStateFlow


    private val _statusLiveData = MutableLiveData<Resource<StatusResult>?>()
    val statusLiveData: LiveData<Resource<StatusResult>?>
        get() = _statusLiveData

    fun clearStatusLiveData(){
        _statusLiveData.value = null
    }


    fun getRobotList(){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                _robotStateFlow.emit(Resource.Loading())
                val result = robotDao.getRobotList()
                _robotStateFlow.emit(Resource.Success(result))
            }catch (e:Exception){
                e.printStackTrace()
                _robotStateFlow.emit(Resource.Error(e.message.toString()))
            }
        }
    }


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
    fun deleteRobotUsingId(robotId: String) {
        try {
            _statusLiveData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                async {
                    robotDao.deleteRobotUsingId(robotId)
                }.await()

                val result = async {
                    robotDao.deleteRobotUsingId(robotId)
                }.await()


                handleResult(result, "Deleted robot and Chat successfully", StatusResult.Deleted)
            }
        } catch (e: Exception) {
            _statusLiveData.postValue(Resource.Error(e.message.toString()))
        }
    }


    fun updateRobot(robot: Robot) {
        try {
            _statusLiveData.postValue(Resource.Loading())
            CoroutineScope(Dispatchers.IO).launch {
                val result = robotDao.updateRobot(robot)
                handleResult(result, "Updated robot successfully", StatusResult.Updated)
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