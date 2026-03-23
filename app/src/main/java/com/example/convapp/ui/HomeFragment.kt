package com.example.convapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.convapp.R
import com.example.convapp.databinding.FragmentHomeBinding
import com.example.convapp.viewmodel.AppViewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Total stars
        binding.tvTotalStars.text = getString(R.string.total_stars, viewModel.getTotalStars())

        // Settings button
        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_settingsFragment)
        }

        // Scenario grid
        val scenarios = viewModel.getScenarios()
        val completedIds = scenarios.filter { viewModel.isScenarioCompleted(it.id) }
            .map { it.id }.toSet()

        binding.rvScenarios.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvScenarios.adapter = ScenarioAdapter(scenarios, completedIds) { scenario ->
            val action = HomeFragmentDirections
                .actionHomeFragmentToConversationFragment(scenario.id)
            findNavController().navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh star count in case user just completed a scenario
        binding.tvTotalStars.text = getString(R.string.total_stars, viewModel.getTotalStars())
        // Refresh adapter for updated completion status
        val scenarios = viewModel.getScenarios()
        val completedIds = scenarios.filter { viewModel.isScenarioCompleted(it.id) }
            .map { it.id }.toSet()
        binding.rvScenarios.adapter = ScenarioAdapter(scenarios, completedIds) { scenario ->
            val action = HomeFragmentDirections
                .actionHomeFragmentToConversationFragment(scenario.id)
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
