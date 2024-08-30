package com.example.jarvisv2.repository

import android.app.Application

class AuthRepository(val application: Application) {
    private val chatRepository = ChatRepository(application)

    fun loginUser(email: String, password: String) {
        // Giriş işlemi kodları

        // Giriş başarılı olduğunda kullanıcıya özel kategorileri oluştur
        chatRepository.createUserSpecificCategory(email)
    }
}