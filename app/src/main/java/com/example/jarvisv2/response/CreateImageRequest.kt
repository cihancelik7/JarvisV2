package com.example.jarvisv2.response

data class CreateImageRequest(
    val n: Int,
    val prompt: String,
    val size: String
)