package com.trainingtimer.views.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.databinding.ListItemTrainingBinding
import com.trainingtimer.domain.Training

class TrainingAdapter : ListAdapter<Training, TrainingViewHolder>(TrainingDiffCallback()) {

    var onTrainingLongClickListener: ((Training) -> Unit)? = null
    var onTrainingClickListener: ((Training) -> Unit)? = null

//    val MAX_POOL_SIZE = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemTrainingBinding.inflate(inflater, parent, false)
        return TrainingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = getItem(position)
        with(holder) {
            listSets.text = training.sets.toString()
            listTitle.text = training.title
            listTimes.text = training.times
            listRest.text = training.rest
        }
        holder.binding.root.setOnClickListener {
            onTrainingClickListener?.invoke(training)
        }
        holder.binding.root.setOnLongClickListener {
            onTrainingLongClickListener?.invoke(training)
            true
        }
    }
}