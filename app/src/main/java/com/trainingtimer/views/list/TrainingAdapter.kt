package com.trainingtimer.views.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.ListItemTrainingBinding
import com.trainingtimer.domain.Training

class TrainingAdapter(
    private val onItemClick: (Training) -> Unit,
    private val onItemLongClick: (Training) -> Unit
) : ListAdapter<Training, TrainingViewHolder>(TrainingDiffCallback()) {

//    val MAX_POOL_SIZE = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTrainingBinding.inflate(inflater, parent, false)
        return TrainingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = getItem(position)
        holder.bind(training, onItemClick, onItemLongClick)
    }
}