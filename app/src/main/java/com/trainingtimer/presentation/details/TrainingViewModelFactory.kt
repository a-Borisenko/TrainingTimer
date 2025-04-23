package com.trainingtimer.presentation.details

import dagger.assisted.AssistedFactory

@AssistedFactory
interface TrainingViewModelFactory {

    fun create(trainingId: Int): TrainingViewModel
}