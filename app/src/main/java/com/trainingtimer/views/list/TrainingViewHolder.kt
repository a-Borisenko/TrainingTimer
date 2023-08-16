package com.trainingtimer.views.list

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R

class TrainingViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    val listTitle = view.findViewById<TextView>(R.id.training_title)
    val listSets = view.findViewById<TextView>(R.id.training_sets)
    val listTimes = view.findViewById<TextView>(R.id.training_times)
    val listRest = view.findViewById<TextView>(R.id.training_rest)

}