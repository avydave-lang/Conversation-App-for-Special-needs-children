package com.example.convapp.model

import com.google.gson.annotations.SerializedName

data class Scenario(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String = "",
    @SerializedName("emoji") val emoji: String,
    @SerializedName("color") val color: String,
    @SerializedName("startNode") val startNode: String,
    @SerializedName("totalSteps") val totalSteps: Int
)
