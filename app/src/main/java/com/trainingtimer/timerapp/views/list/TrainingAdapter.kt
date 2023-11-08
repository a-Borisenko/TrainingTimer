package com.trainingtimer.timerapp.views.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.R
import com.trainingtimer.foundation.domain.Training

class TrainingAdapter : ListAdapter<Training, TrainingViewHolder>(TrainingDiffCallback()) {

    var onTrainingLongClickListener: ((Training) -> Unit)? = null
    var onTrainingClickListener: ((Training) -> Unit)? = null

    val MAX_POOL_SIZE = 10

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_training,
            parent,
            false
        )
        return TrainingViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        val training = getItem(position)
        with(holder) {
            listSets.text = training.sets.toString()
            listTitle.text = training.title
            listTimes.text = training.times
            listRest.text = training.rest
        }
        holder.view.setOnClickListener {
            onTrainingClickListener?.invoke(training)
        }
        holder.view.setOnLongClickListener {
            onTrainingLongClickListener?.invoke(training)
            true
        }
    }
}