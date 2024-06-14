package com.trainingtimer.views.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.trainingtimer.R
import com.trainingtimer.databinding.SplashScreenBinding
import com.trainingtimer.views.list.TrainingListFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment(R.layout.splash_screen) {

//    private val viewModel: SplashViewModel by viewModels()
    private lateinit var binding: SplashScreenBinding
    private var progressBarStatus = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SplashScreenBinding.bind(view)

        // Наблюдайте за состоянием загрузки данных
        lifecycleScope.launch {
            while (progressBarStatus < 100) {
                progressBarStatus += 25
                delay(100)
                binding.splashScreenProgressBar.progress = progressBarStatus
            }
            navigateToMainScreen()
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

        // Если возвращаемся на этот экран, обновить данные
        /*lifecycleScope.launch {
            viewModel.updateData()
        }*/
    }

    private fun navigateToMainScreen() {
        // Перейти к основному экрану приложения
        requireActivity().supportFragmentManager
            .popBackStack()
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, TrainingListFragment())
            .commit()
    }

    /*private fun showErrorMessage(error: Throwable) {
        // Показать сообщение об ошибке пользователю
        Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
    }*/
}