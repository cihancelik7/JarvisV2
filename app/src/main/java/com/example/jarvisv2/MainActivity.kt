package com.example.jarvisv2

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.jarvisv2.utils.NetworkConnectivityObserver
import com.example.jarvisv2.utils.NetworkStatus
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    private val networkConnectivityObserver: NetworkConnectivityObserver by lazy {
        NetworkConnectivityObserver(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Show the splash screen
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Continue showing the splash screen until data is loaded
        splashScreen.setKeepOnScreenCondition { false }

        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "No Internet Connection",
            Snackbar.LENGTH_INDEFINITE
        ).setAction("Wifi") {
            startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
        }

        networkConnectivityObserver.observe(this) {
            when (it) {
                NetworkStatus.Available -> {

                }

                else -> {
                    snackbar.show()
                }
            }
        }
    }
}
