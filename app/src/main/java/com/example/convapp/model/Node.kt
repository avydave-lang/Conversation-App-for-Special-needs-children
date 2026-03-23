package com.example.convapp.model

import com.google.gson.annotations.SerializedName

data class Node(
    @SerializedName("nodeId") val nodeId: String,
    @SerializedName("type") val type: String,           // prompt | teach | bridge | celebrate
    @SerializedName("say") val say: String,
    @SerializedName("hint") val hint: String? = null,
    @SerializedName("visual") val visual: String = "nod", // wave|nod|think|cheer|sad|point
    @SerializedName("expectedKeywords") val expectedKeywords: List<String> = emptyList(),
    @SerializedName("minKeywordsRequired") val minKeywordsRequired: Int = 1,
    @SerializedName("successNode") val successNode: String? = null,
    @SerializedName("retryNode") val retryNode: String? = null,
    @SerializedName("failNode") val failNode: String? = null,
    @SerializedName("maxRetries") val maxRetries: Int = 2,
    @SerializedName("isTerminal") val isTerminal: Boolean = false,
    @SerializedName("starValue") val starValue: Int = 0,
    @SerializedName("scenarioId") val scenarioId: String
)
