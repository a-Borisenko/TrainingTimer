package com.trainingtimer.views.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.trainingtimer.R
import com.trainingtimer.databinding.SplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.splash_screen) {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var binding: SplashScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashScreenBinding.bind(view)

        lifecycleScope.launch {
            viewModel.progressFlow.collect { progress ->
                binding.splashScreenProgressBar.progress = progress
                if (progress == 100) {
                    navigateToMainScreen()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.startSplashTimer()
        }
    }

    private fun navigateToMainScreen() {
        findNavController().navigate(
            R.id.action_splashFragment_to_trainingListFragment
        )
    }
}