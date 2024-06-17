package com.trainingtimer.views.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _progressFlow = MutableStateFlow(0)
    val progressFlow: StateFlow<Int> = _progressFlow.asStateFlow()

    private var dummy: Int = 0

    fun startSplashTimer() {
        viewModelScope.launch {
            while (_progressFlow.value < 100) {
                delay(100)
                dummy += 25
                _progressFlow.value = dummy
            }
        }
    }

    /*private val _loadingStateFlow = MutableStateFlow<LoadingState>(LoadingState.Loading)
    val loadingStateFlow: StateFlow<LoadingState> = _loadingStateFlow.asStateFlow()

    fun updateData() {
        viewModelScope.launch {
            try {
                // Загрузка данных
                val data = fetchData()
                _loadingStateFlow.value = LoadingState.Success(data)
            } catch (e: Exception) {
                _loadingStateFlow.value = LoadingState.Error(e)
            }
        }
    }

    private suspend fun fetchData(): Data {
        // Реализация загрузки данных
        delay(2000) // Имитация загрузки данных
        return Data()
    }*/
}