package com.example.convapp.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.convapp.databinding.ItemScenarioCardBinding
import com.example.convapp.model.Scenario

class ScenarioAdapter(
    private val scenarios: List<Scenario>,
    private val completedIds: Set<String>,
    private val onClick: (Scenario) -> Unit
) : RecyclerView.Adapter<ScenarioAdapter.ScenarioViewHolder>() {

    inner class ScenarioViewHolder(val binding: ItemScenarioCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val binding = ItemScenarioCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ScenarioViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        val scenario = scenarios[position]
        val completed = scenario.id in completedIds
        with(holder.binding) {
            tvEmoji.text = scenario.emoji
            tvTitle.text = scenario.title
            tvStar.text = if (completed) "⭐" else "☆"
            tvStar.alpha = if (completed) 1f else 0.4f
            try {
                root.setCardBackgroundColor(Color.parseColor(scenario.color))
            } catch (e: IllegalArgumentException) {
                // keep default color
            }
            root.setOnClickListener { onClick(scenario) }
        }
    }

    override fun getItemCount() = scenarios.size
}
