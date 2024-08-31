package com.example.jarvisv2.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Robot (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "robotId")
    var robotId: String = "",  // Varsayılan değer ekledik
    var robotName: String = "",  // Varsayılan değer ekledik
    var robotImg: Int = 0  // Varsayılan değer ekledik
) {
    // Room ve Firebase'in sorunsuz çalışabilmesi için parametresiz yapıcı sağlıyoruz
    constructor() : this("", "", 0)
}