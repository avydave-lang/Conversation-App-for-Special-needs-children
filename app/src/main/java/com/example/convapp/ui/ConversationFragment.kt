package com.example.convapp.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.convapp.R
import com.example.convapp.databinding.FragmentConversationBinding
import com.example.convapp.model.Node
import com.example.convapp.viewmodel.AppViewModel
import com.example.convapp.viewmodel.MicState

class ConversationFragment : Fragment() {

    private var _binding: FragmentConversationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()
    private val args: ConversationFragmentArgs by navArgs()

    private val micPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.handleMicTap()
        } else {
            binding.tvTranscript.text = getString(R.string.mic_permission_denied)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConversationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadScenario(args.scenarioId)

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }

        binding.btnMic.setOnClickListener { onMicTapped() }

        binding.btnHint.setOnClickListener { viewModel.handleHintRequested() }

        // Observe current node
        viewModel.currentNode.observe(viewLifecycleOwner) { node ->
            node ?: return@observe
            updateSpeechBubble(node)
            updateAvatar(node)
        }

        // Observe mic state
        viewModel.micState.observe(viewLifecycleOwner) { state ->
            updateMicButton(state)
        }

        // Observe transcript
        viewModel.transcript.observe(viewLifecycleOwner) { text ->
            if (text.isNotEmpty()) {
                binding.tvTranscript.text = getString(R.string.heard_prefix, text)
            }
        }

        // Observe step progress
        viewModel.stepProgress.observe(viewLifecycleOwner) { step ->
            val total = viewModel.currentScenario.value?.totalSteps ?: 5
            binding.tvStep.text = getString(R.string.step_counter, step, total)
            binding.progressBar.max = total
            binding.progressBar.progress = step
        }

        // Observe scenario name
        viewModel.currentScenario.observe(viewLifecycleOwner) { scenario ->
            binding.tvScenarioTitle.text = scenario?.title ?: ""
        }

        // Navigate to results
        viewModel.navigateToResults.observe(viewLifecycleOwner) { shouldNavigate ->
            if (shouldNavigate) {
                viewModel.onNavigateToResultsHandled()
                val stars = viewModel.sessionStars.value ?: 1
                val action = ConversationFragmentDirections
                    .actionConversationFragmentToResultsFragment(args.scenarioId, stars)
                findNavController().navigate(action)
            }
        }
    }

    private fun onMicTapped() {
        if (ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else {
            viewModel.handleMicTap()
        }
    }

    private fun updateSpeechBubble(node: Node) {
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in)
        binding.tvSpeechBubble.startAnimation(fadeIn)
        binding.tvSpeechBubble.text = node.say
        binding.tvTranscript.text = ""
        // Show hint button only for prompt nodes
        binding.btnHint.visibility = if (node.type == "prompt" && node.hint != null)
            View.VISIBLE else View.INVISIBLE
    }

    private fun updateAvatar(node: Node) {
        val emojiMap = mapOf(
            "wave"  to "👋",
            "nod"   to "😊",
            "think" to "🤔",
            "cheer" to "🎉",
            "sad"   to "😢",
            "point" to "👉"
        )
        binding.tvAvatar.text = emojiMap[node.visual] ?: "😊"
    }

    private fun updateMicButton(state: MicState) {
        when (state) {
            MicState.IDLE -> {
                binding.btnMic.clearAnimation()
                binding.btnMic.text = getString(R.string.tap_to_talk)
                binding.btnMic.isEnabled = true
                binding.btnMic.setBackgroundResource(R.drawable.bg_mic_idle)
            }
            MicState.LISTENING -> {
                val pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.mic_pulse)
                binding.btnMic.startAnimation(pulse)
                binding.btnMic.text = getString(R.string.listening)
                binding.btnMic.isEnabled = true
                binding.btnMic.setBackgroundResource(R.drawable.bg_mic_listening)
            }
            MicState.PROCESSING -> {
                binding.btnMic.clearAnimation()
                binding.btnMic.text = getString(R.string.processing)
                binding.btnMic.isEnabled = false
                binding.btnMic.setBackgroundResource(R.drawable.bg_mic_idle)
            }
            MicState.ERROR -> {
                binding.btnMic.clearAnimation()
                binding.btnMic.text = getString(R.string.mic_unavailable)
                binding.btnMic.isEnabled = false
                binding.btnMic.setBackgroundResource(R.drawable.bg_mic_error)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
