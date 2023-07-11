package com.trainingtimer.ui.list

import androidx.recyclerview.widget.DiffUtil
import com.trainingtimer.domain.Training

class TrainingDiffCallback: DiffUtil.ItemCallback<Training>() {

    override fun areItemsTheSame(oldItem: Training, newItem: Training): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Training, newItem: Training): Boolean {
        return oldItem == newItem
    }
}