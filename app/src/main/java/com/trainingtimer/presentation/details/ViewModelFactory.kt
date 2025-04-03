package com.trainingtimer.presentation.details

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.trainingtimer.domain.usecases.AddTrainingUseCase
import com.trainingtimer.domain.usecases.EditTrainingUseCase
import com.trainingtimer.domain.usecases.GetTrainingListUseCase
import com.trainingtimer.domain.usecases.GetTrainingUseCase
import javax.inject.Inject

class ViewModelFactory @Inject constructor(
    private val getTrainingUseCase: GetTrainingUseCase,
    private val addTrainingUseCase: AddTrainingUseCase,
    private val editTrainingUseCase: EditTrainingUseCase,
    private val getTrainingListUseCase: GetTrainingListUseCase,
    private val application: Application,
    private val id: Int
) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrainingViewModel::class.java)) {
            return TrainingViewModel(getTrainingUseCase, addTrainingUseCase, editTrainingUseCase, getTrainingListUseCase, application, id) as T
        }
        throw RuntimeException("Unknown viewModel class $modelClass")
    }
}