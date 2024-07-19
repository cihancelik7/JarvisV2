package com.example.jarvisv2.network

import com.example.jarvisv2.utils.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    @Volatile
    private var INSTANCE: ApiInterface? = null

    fun getInstance(): ApiInterface {
        synchronized(this) {
            if (INSTANCE == null) {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build()

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)  // Ensure BASE_URL ends with a trailing slash
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiInterface::class.java)
            }
            return INSTANCE!!
        }
    }
}