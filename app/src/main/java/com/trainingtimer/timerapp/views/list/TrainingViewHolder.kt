package com.trainingtimer.timerapp.views.list

import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.ListItemTrainingBinding

class TrainingViewHolder(val binding: ListItemTrainingBinding) : RecyclerView.ViewHolder(binding.root) {

    val listTitle = binding.trainingTitle
    val listSets = binding.trainingSets
    val listTimes = binding.trainingTimes
    val listRest = binding.trainingRest

}