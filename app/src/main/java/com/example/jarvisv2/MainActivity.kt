package com.example.jarvisv2

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val splashScreen = installSplashScreen()

        // here setKeepOnScreenCondition false so, activity redirect another activity
        // and some api call here
        // if setKeepOnScreenCondition true so, activity code not redirect another activity
        splashScreen.setKeepOnScreenCondition { false }
//        Handler(Looper.getMainLooper()).postDelayed({
//            startActivity(Intent(this, SecondActivity::class.java))
//            finish()
//        }, 2000)


    }
}