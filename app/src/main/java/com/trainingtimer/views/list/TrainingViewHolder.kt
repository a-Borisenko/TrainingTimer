package com.trainingtimer.views.list

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.ListItemTrainingBinding
import com.trainingtimer.domain.Training

class TrainingViewHolder(val binding: ListItemTrainingBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(training: Training) {
        binding.apply {
            trainingSets.text = training.sets.toString()
            trainingTitle.text = training.title
            trainingTimes.text = training.times
            trainingRest.text = training.rest
        }
    }
}