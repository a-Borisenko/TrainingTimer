package com.trainingtimer.views.splash

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.trainingtimer.MainActivity
import com.trainingtimer.R
import com.trainingtimer.databinding.SplashScreenBinding
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.splash_screen) {

    private val viewModel: SplashViewModel by viewModels()
    private lateinit var binding: SplashScreenBinding
//    private var progressBarStatus = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashScreenBinding.bind(view)

        // Наблюдайте за состоянием загрузки данных
        lifecycleScope.launch {
            viewModel.progressFlow.collect { progress ->
                binding.splashScreenProgressBar.progress = progress
                if (progress == 100) {
                    launchMainActivity()
                }
            }
//            navigateToMainScreen()
            /*viewModel.loadingStateFlow.collect { state ->
                when (state) {
                    is LoadingState.Loading -> {
                        // Показать индикатор загрузки
                        binding.splashScreenProgressBar.show()
                    }
                    is LoadingState.Success -> {
                        // Скрыть индикатор загрузки и перейти к основному экрану
                        binding.splashScreenProgressBar.hide()
                        navigateToMainScreen()
                    }
                    is LoadingState.Error -> {
                        // Показать ошибку пользователю
                        binding.splashScreenProgressBar.hide()
                        showErrorMessage(state.error)
                    }
                }
            }*/
        }

        lifecycleScope.launch {
            viewModel.startSplashTimer()
        }

        // Если возвращаемся на этот экран, обновить данные
        /*lifecycleScope.launch {
            viewModel.updateData()
        }*/
    }

    private fun launchMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        requireActivity().finish()
    }

    /*private fun navigateToMainScreen() {
        // Перейти к основному экрану приложения
        requireActivity().supportFragmentManager
            .popBackStack()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, TrainingListFragment())
            .commit()
    }*/

    /*private fun showErrorMessage(error: Throwable) {
        // Показать сообщение об ошибке пользователю
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }*/
}