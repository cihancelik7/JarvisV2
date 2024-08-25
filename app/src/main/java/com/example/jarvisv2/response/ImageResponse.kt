package com.example.jarvisv2.response

import com.google.gson.annotations.SerializedName

data class ImageResponse(
    val created: Int,
    @SerializedName("data")
    val data: List<Data>
)

data class Data(
    val url: String
)