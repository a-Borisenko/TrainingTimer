package com.trainingtimer.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.trainingtimer.R
import com.trainingtimer.domain.Training

class TrainingAdapter : ListAdapter<Training, TrainingViewHolder>(TrainingDiffCallback()) {

    var onShopItemLongClickListener: ((Training) -> Unit)? = null
    var onShopItemClickListener: ((Training) -> Unit)? = null

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
            listSets.text = training.sets
            listTitle.text = training.title
            listTimes.text = training.times
            listRest.text = training.rest
        }
        holder.view.setOnClickListener {
            onShopItemClickListener?.invoke(training)
        }
        holder.view.setOnLongClickListener {
            onShopItemLongClickListener?.invoke(training)
            true
        }
    }

    /*private inner class TrainingAdapter(var trainings: List<Training>) :
        RecyclerView.Adapter<TrainingHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : TrainingHolder {
            val view = layoutInflater.inflate(R.layout.list_item_training, parent, false)
            return TrainingHolder(view)
        }

        override fun onBindViewHolder(holder: TrainingHolder, position: Int) {
            val training = trainings[position]
            holder.bind(training)
        }

        override fun getItemCount() = trainings.size
    }*/
}