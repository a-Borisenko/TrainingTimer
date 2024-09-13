package com.trainingtimer.views.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.databinding.ListItemTrainingBinding
import com.trainingtimer.domain.Training

class TrainingAdapter : ListAdapter<Training, TrainingViewHolder>(TrainingDiffCallback()) {

    var onTrainingLongClickListener: ((Training) -> Unit)? = null
    var onTrainingClickListener: ((Training) -> Unit)? = null

//    val MAX_POOL_SIZE = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTrainingBinding.inflate(inflater, parent, false)
        return TrainingViewHolder(binding).apply {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTrainingClickListener?.invoke(getItem(position))
                }
            }
            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onTrainingLongClickListener?.invoke(getItem(position))
                    true
                } else {
                    false
                }
            }
        }
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = getItem(position)
        holder.bind(training)
    }
}