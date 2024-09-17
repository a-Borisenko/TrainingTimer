package com.trainingtimer.views.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.domain.DeleteTrainingUseCase
import com.trainingtimer.domain.GetTrainingListUseCase
import com.trainingtimer.domain.Training
import com.trainingtimer.utils.DataService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingListViewModel @Inject constructor(
    getTrainingListUseCase: GetTrainingListUseCase,
    private val deleteTrainingUseCase: DeleteTrainingUseCase
) : ViewModel() {

    val trainingList = getTrainingListUseCase.getTrainingList()

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