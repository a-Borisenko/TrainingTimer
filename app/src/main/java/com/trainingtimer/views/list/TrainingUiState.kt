package com.trainingtimer.views.list

sealed class TrainingUiState {
    object Loading : TrainingUiState()
    object Loaded : TrainingUiState()
}
