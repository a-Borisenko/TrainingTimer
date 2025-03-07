package com.trainingtimer.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trainingtimer.domain.entity.Training
import com.trainingtimer.domain.usecases.DeleteTrainingUseCase
import com.trainingtimer.domain.usecases.GetTrainingListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
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

    val trainingList = MutableStateFlow<List<Training>>(emptyList())

    private val _uiState = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
    val uiState: StateFlow<TrainingUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getTrainingListUseCase.getTrainingList()
                .stateIn(viewModelScope, WhileSubscribed(), emptyList())
                .collect {
                    trainingList.value = it
                    if (it.isNotEmpty()) {
                        _uiState.value = TrainingUiState.Loaded
                    }
                }
        }
    }

    fun deleteTraining(training: Training) {
        viewModelScope.launch {
            deleteTrainingUseCase.deleteTraining(training)
        }
    }
}