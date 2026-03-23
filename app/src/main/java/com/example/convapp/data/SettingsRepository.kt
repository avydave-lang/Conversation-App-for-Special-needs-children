package com.example.convapp.data

import android.content.Context
import com.example.convapp.model.AppSettings

class SettingsRepository(context: Context) {

    private val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun load(): AppSettings = AppSettings(
        speechRate = prefs.getFloat("speech_rate", 0.85f),
        pitch = prefs.getFloat("pitch", 1.1f),
        autoMic = prefs.getBoolean("auto_mic", false),
        fontSizeLevel = prefs.getInt("font_size_level", 0),
        highContrast = prefs.getBoolean("high_contrast", false)
    )

    fun save(settings: AppSettings) {
        prefs.edit()
            .putFloat("speech_rate", settings.speechRate)
            .putFloat("pitch", settings.pitch)
            .putBoolean("auto_mic", settings.autoMic)
            .putInt("font_size_level", settings.fontSizeLevel)
            .putBoolean("high_contrast", settings.highContrast)
            .apply()
    }
}
