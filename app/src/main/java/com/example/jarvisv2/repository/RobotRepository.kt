package com.example.jarvisv2.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.jarvisv2.models.Robot
import com.example.jarvisv2.utils.Resource
import com.example.jarvisv2.utils.StatusResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class RobotRepository(application: Application) {

    private val _robotStateFlow = MutableStateFlow<Resource<List<Robot>>>(Resource.Loading())
    val robotStateFlow: StateFlow<Resource<List<Robot>>> get() = _robotStateFlow

    private val _statusLiveData = MutableLiveData<Resource<StatusResult>?>()
    val statusLiveData: LiveData<Resource<StatusResult>?> get() = _statusLiveData

    private val firebaseDatabase = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Kullanıcının UID'sini almak için
    private fun getUserUid(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User is not logged in")
    }

    // Firebase'e robot ekleme işlemi
    fun insertRobot(robot: Robot) {
        val uid = getUserUid()
        val userCategoryRef = firebaseDatabase.child("users").child(uid).child("robots")

        userCategoryRef.child(robot.robotId).setValue(robot)
            .addOnSuccessListener {
                _statusLiveData.postValue(Resource.Success(StatusResult.Added, "Robot added successfully"))
            }
            .addOnFailureListener { exception ->
                _statusLiveData.postValue(Resource.Error(exception.message.toString()))
            }
    }

    // Firebase'den robotları getirme işlemi
    fun getRobotList() {
        val uid = getUserUid()
        val userCategoryRef = firebaseDatabase.child("users").child(uid).child("robots")

        userCategoryRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val robotList = mutableListOf<Robot>()
                snapshot.children.forEach { childSnapshot ->
                    val robot = childSnapshot.getValue(Robot::class.java)
                    if (robot != null) {
                        robotList.add(robot)
                    }
                }
                _robotStateFlow.value = Resource.Success(robotList)
            }

            override fun onCancelled(error: DatabaseError) {
                _robotStateFlow.value = Resource.Error(error.message)
            }
        })
    }

    // Firebase'de robot güncelleme işlemi
    fun updateRobot(robot: Robot) {
        val uid = getUserUid()
        val userCategoryRef = firebaseDatabase.child("users").child(uid).child("robots").child(robot.robotId)

        userCategoryRef.setValue(robot)
            .addOnSuccessListener {
                _statusLiveData.postValue(Resource.Success(StatusResult.Updated, "Robot updated successfully"))
            }
            .addOnFailureListener { exception ->
                _statusLiveData.postValue(Resource.Error(exception.message.toString()))
            }
    }

    // Firebase'de robot silme işlemi
    fun deleteRobotUsingId(robotId: String) {
        val uid = getUserUid()
        val userCategoryRef = firebaseDatabase.child("users").child(uid).child("robots").child(robotId)

        userCategoryRef.removeValue()
            .addOnSuccessListener {
                _statusLiveData.postValue(Resource.Success(StatusResult.Deleted, "Robot deleted successfully"))
            }
            .addOnFailureListener { exception ->
                _statusLiveData.postValue(Resource.Error(exception.message.toString()))
            }
    }

    fun clearStatusLiveData() {
        _statusLiveData.value = null
    }
}