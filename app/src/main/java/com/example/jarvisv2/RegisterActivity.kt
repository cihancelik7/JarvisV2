package com.example.jarvisv2

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.jarvisv2.databinding.ActivityRegisterBinding
import com.example.jarvisv2.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var firebaseRepository: FirebaseRepository  // FirebaseRepository'yi tanımla

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()
        firebaseRepository = FirebaseRepository()  // FirebaseRepository'yi başlat

        binding.showPasswordCheckBox1.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.registerPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.registerPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.registerPasswordConfirmEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                binding.registerPasswordConfirmEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.registerButton.setOnClickListener {
            registerUser()
        }

        binding.loginTextView.setOnClickListener {
            finish()
        }
    }

    private fun registerUser() {
        val firstName = binding.firstNameEditText.text.toString().trim()
        val lastName = binding.lastNameEditText.text.toString().trim()
        val email = binding.registerEmailEditText.text.toString().trim()
        val password = binding.registerPasswordEditText.text.toString().trim()
        val confirmPassword = binding.registerPasswordConfirmEditText.text.toString().trim()

        if (firstName.isEmpty()) {
            binding.firstNameEditText.error = "First name is required"
            binding.firstNameEditText.requestFocus()
            return
        }

        if (lastName.isEmpty()) {
            binding.lastNameEditText.error = "Last name is required"
            binding.lastNameEditText.requestFocus()
            return
        }

        if (email.isEmpty()) {
            binding.registerEmailEditText.error = "Email is required"
            binding.registerEmailEditText.requestFocus()
            return
        }

        if (password.isEmpty()) {
            binding.registerPasswordEditText.error = "Password is required"
            binding.registerPasswordEditText.requestFocus()
            return
        }

        if (password != confirmPassword) {
            binding.registerPasswordConfirmEditText.error = "Passwords do not match"
            binding.registerPasswordConfirmEditText.requestFocus()
            return
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Kayıt başarılı, kullanıcıya özel kategorileri oluştur
                    firebaseRepository.createUserSpecificCategory()

                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    if (task.exception is FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "This email is already registered.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
}