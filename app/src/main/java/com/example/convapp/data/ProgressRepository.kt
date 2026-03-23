package com.example.convapp.data

import android.content.Context

class ProgressRepository(context: Context) {

    private val prefs = context.getSharedPreferences("progress", Context.MODE_PRIVATE)

    fun getTotalStars(): Int = prefs.getInt("total_stars", 0)

    fun addStar() {
        prefs.edit().putInt("total_stars", getTotalStars() + 1).apply()
    }

    fun isScenarioCompleted(scenarioId: String): Boolean =
        prefs.getBoolean("completed_$scenarioId", false)

    fun markScenarioCompleted(scenarioId: String) {
        prefs.edit().putBoolean("completed_$scenarioId", true).apply()
    }

    fun resetAll() {
        prefs.edit().clear().apply()
    }
}
