package com.example.jarvisv2.repository

import com.example.jarvisv2.models.Robot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class FirebaseRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Kullanıcının UID'sini almak için
    private fun getUserUid(): String {
        return auth.currentUser?.uid ?: throw IllegalStateException("User is not logged in")
    }

    // Kullanıcıya özel robot referansı
    private fun getUserRobotRef(): DatabaseReference {
        val uid = getUserUid()
        return database.getReference("users/$uid/robots")
    }

    // Kullanıcıya özel chat, image ve robot kategorileri oluşturma fonksiyonu
    fun createUserSpecificCategory() {
        val uid = getUserUid()
        val userCategoryRef = database.getReference("users/$uid")

        // Chat kategorisi oluştur
        userCategoryRef.child("chats").setValue(true)

        // Image kategorisi oluştur
        userCategoryRef.child("images").setValue(true)

        // Robots kategorisi oluştur
        userCategoryRef.child("robots").setValue(true)
    }

    // Robot ekleme fonksiyonu
    fun addRobot(robot: Robot) {
        val robotRef = getUserRobotRef().child(robot.robotId)
        robotRef.setValue(robot)
    }

    // Kullanıcıya özel robot chat listesini getirme
    fun getUserRobotChats(robotName: String): DatabaseReference {
        return getUserRobotRef().child(robotName)
    }
}