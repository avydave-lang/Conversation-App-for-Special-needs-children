package com.example.convapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.convapp.R
import com.example.convapp.databinding.FragmentResultsBinding
import com.example.convapp.viewmodel.AppViewModel

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppViewModel by activityViewModels()
    private val args: ResultsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvTotalStars.text =
            getString(R.string.total_stars, viewModel.getTotalStars())

        // Animate star in with bounce
        animateStar()

        // Play again
        binding.btnPlayAgain.setOnClickListener {
            val action = ResultsFragmentDirections
                .actionResultsFragmentToConversationFragment(args.scenarioId)
            findNavController().navigate(action)
        }

        // Home
        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.action_resultsFragment_to_homeFragment)
        }
    }

    private fun animateStar() {
        val star = binding.tvStarReward
        star.translationY = 300f
        star.alpha = 0f

        val moveUp = ObjectAnimator.ofFloat(star, "translationY", 300f, 0f).apply {
            duration = 700
            interpolator = BounceInterpolator()
        }
        val fadeIn = ObjectAnimator.ofFloat(star, "alpha", 0f, 1f).apply {
            duration = 400
        }
        val scaleX = ObjectAnimator.ofFloat(star, "scaleX", 0.3f, 1f).apply {
            duration = 600
        }
        val scaleY = ObjectAnimator.ofFloat(star, "scaleY", 0.3f, 1f).apply {
            duration = 600
        }

        AnimatorSet().apply {
            playTogether(moveUp, fadeIn, scaleX, scaleY)
            startDelay = 300
            start()
        }

        // Pulse celebration text
        Handler(Looper.getMainLooper()).postDelayed({
            if (_binding != null) {
                val pulse = ObjectAnimator.ofFloat(binding.tvCelebrate, "scaleX", 1f, 1.1f, 1f)
                pulse.duration = 500
                val pulseY = ObjectAnimator.ofFloat(binding.tvCelebrate, "scaleY", 1f, 1.1f, 1f)
                pulseY.duration = 500
                AnimatorSet().apply {
                    playTogether(pulse, pulseY)
                    start()
                }
            }
        }, 800)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
