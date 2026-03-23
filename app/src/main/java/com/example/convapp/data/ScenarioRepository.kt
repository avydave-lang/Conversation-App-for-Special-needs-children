package com.example.convapp.data

import android.content.Context
import com.example.convapp.model.Node
import com.example.convapp.model.Scenario
import com.google.gson.Gson
import com.google.gson.JsonObject

class ScenarioRepository(private val context: Context) {

    private var cachedScenarios: List<Scenario>? = null
    private var cachedNodes: Map<String, Node>? = null

    private fun load() {
        if (cachedScenarios != null) return
        val json = context.assets.open("scenarios.json").bufferedReader().readText()
        val gson = Gson()
        val root = gson.fromJson(json, JsonObject::class.java)

        val scenarios = gson.fromJson(
            root.getAsJsonArray("scenarios"),
            Array<Scenario>::class.java
        ).toList()

        val nodesObj = root.getAsJsonObject("nodes")
        val nodes = mutableMapOf<String, Node>()
        nodesObj.keySet().forEach { key ->
            nodes[key] = gson.fromJson(nodesObj.get(key), Node::class.java)
        }

        cachedScenarios = scenarios
        cachedNodes = nodes
    }

    fun getScenarios(): List<Scenario> {
        load()
        return cachedScenarios!!
    }

    fun getNodes(): Map<String, Node> {
        load()
        return cachedNodes!!
    }

    fun getScenario(id: String): Scenario? = getScenarios().find { it.id == id }
}
