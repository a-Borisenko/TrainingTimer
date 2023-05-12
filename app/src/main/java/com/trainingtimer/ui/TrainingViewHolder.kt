package com.trainingtimer.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R

class TrainingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
    val listTitle = view.findViewById<TextView>(R.id.training_title)
    val listSets = view.findViewById<TextView>(R.id.training_sets)
    val listTimes = view.findViewById<TextView>(R.id.training_times)
    val listRest = view.findViewById<TextView>(R.id.training_rest)

    /*private inner class TrainingHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var training: Training
        private val titleTextView: TextView = itemView.findViewById(R.id.training_title)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(training: Training) {
            this.training = training
            titleTextView.text = this.training.title
        }

        override fun onClick(v: View) {
            Toast.makeText(context, "${training.title} pressed!", Toast.LENGTH_SHORT)
                .show()
            callbacks?.onTrainingSelected(training.id)
        }
    }*/
}