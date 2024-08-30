package com.example.jarvisv2.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.jarvisv2.repository.AuthRepository

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val authRepository = AuthRepository(application)

    fun login(email: String, password: String) {
        authRepository.loginUser(email, password)
    }
}