package com.trainingtimer.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.domain.usecases.DeleteTrainingUseCase
import com.trainingtimer.domain.usecases.GetTrainingListUseCase
import com.trainingtimer.domain.entity.Training
import com.trainingtimer.utils.DataService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    getTrainingListUseCase: GetTrainingListUseCase,
    private val deleteTrainingUseCase: DeleteTrainingUseCase
) : ViewModel() {

    val trainingList = getTrainingListUseCase.getTrainingList()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _uiState = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    fun loadView() {
        viewModelScope.launch {
            if (DataService.needLoading) {
                _uiState.value = TrainingUiState.Loading
                delay(2000)
            }
            _uiState.value = TrainingUiState.Loaded
            DataService.needLoading = false
        }
    }

    fun deleteTraining(training: Training) {
        viewModelScope.launch {
            deleteTrainingUseCase.deleteTraining(training)
        }
    }
}