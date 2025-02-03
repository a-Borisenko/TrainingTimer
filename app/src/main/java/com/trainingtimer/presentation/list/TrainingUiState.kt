package com.trainingtimer.presentation.list

sealed class TrainingUiState {
    object Loading : TrainingUiState()
    object Loaded : TrainingUiState()
}
