package com.example.convapp.model

data class AppSettings(
    val speechRate: Float = 0.85f,
    val pitch: Float = 1.1f,
    val autoMic: Boolean = false,
    val fontSizeLevel: Int = 0,       // 0=Normal, 1=Large, 2=XL
    val highContrast: Boolean = false
)
