package com.example.convapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.convapp.R
import com.example.convapp.databinding.FragmentSettingsBinding
import com.example.convapp.model.AppSettings
import com.example.convapp.viewmodel.AppViewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settings = viewModel.settings.value ?: AppSettings()

        // Speech rate: map 0.6–1.2 to seekbar 0–6 (×10 steps of 0.1)
        val rateProgress = ((settings.speechRate - 0.6f) / 0.1f).toInt().coerceIn(0, 6)
        binding.seekbarRate.max = 6
        binding.seekbarRate.progress = rateProgress
        updateRateLabel(settings.speechRate)

        binding.seekbarRate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rate = 0.6f + progress * 0.1f
                updateRateLabel(rate)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Auto-mic
        binding.switchAutoMic.isChecked = settings.autoMic

        // High contrast
        binding.switchHighContrast.isChecked = settings.highContrast

        // Font size radio
        when (settings.fontSizeLevel) {
            0 -> binding.radioFontNormal.isChecked = true
            1 -> binding.radioFontLarge.isChecked = true
            2 -> binding.radioFontXl.isChecked = true
        }

        // Save button
        binding.btnSave.setOnClickListener {
            saveSettings()
            findNavController().navigateUp()
        }

        // Back
        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        // Reset progress
        binding.btnReset.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.reset_progress))
                .setMessage(getString(R.string.reset_confirm_message))
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.are_you_sure))
                        .setMessage(getString(R.string.reset_final_message))
                        .setPositiveButton(getString(R.string.yes)) { _, _ ->
                            viewModel.resetAllProgress()
                        }
                        .setNegativeButton(getString(R.string.cancel), null)
                        .show()
                }
                .setNegativeButton(getString(R.string.cancel), null)
                .show()
        }
    }

    private fun updateRateLabel(rate: Float) {
        val label = when {
            rate <= 0.7f -> getString(R.string.rate_slow)
            rate <= 0.9f -> getString(R.string.rate_normal)
            else -> getString(R.string.rate_fast)
        }
        binding.tvRateLabel.text = getString(R.string.speech_rate_label, label)
    }

    private fun saveSettings() {
        val rate = 0.6f + binding.seekbarRate.progress * 0.1f
        val fontLevel = when {
            binding.radioFontLarge.isChecked -> 1
            binding.radioFontXl.isChecked -> 2
            else -> 0
        }
        viewModel.updateSettings(
            AppSettings(
                speechRate = rate,
                autoMic = binding.switchAutoMic.isChecked,
                fontSizeLevel = fontLevel,
                highContrast = binding.switchHighContrast.isChecked
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
