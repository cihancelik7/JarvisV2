package com.example.jarvisv2.network

import com.example.jarvisv2.utils.BASE_URL
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Time
import java.util.concurrent.TimeUnit

object ApiClient {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(1,TimeUnit.MINUTES)
        .readTimeout(60,TimeUnit.SECONDS)
        .writeTimeout(60,TimeUnit.SECONDS)
        .build()

    @Volatile
    private var INSTANCE: ApiInterface? = null

    fun getInstance(): ApiInterface {
        synchronized(this) {

                INSTANCE = Retrofit.Builder()
                    .baseUrl(BASE_URL)  // Ensure BASE_URL ends with a trailing slash
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiInterface::class.java)
            }
            return INSTANCE!!
        }
}