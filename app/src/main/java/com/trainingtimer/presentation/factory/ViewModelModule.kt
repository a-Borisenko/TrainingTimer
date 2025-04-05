package com.trainingtimer.presentation.factory

import androidx.lifecycle.ViewModel
import com.trainingtimer.presentation.details.TrainingViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(TrainingViewModel::class)
    @Binds
    fun bindTrainingViewModel(impl: TrainingViewModel): ViewModel
}