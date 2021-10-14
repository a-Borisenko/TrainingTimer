package com.trainingtimer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

private const val TAG = "TrainingListFragment"

class TrainingListFragment : Fragment() {

    private val trainingListViewModel: TrainingListViewModel by lazy {
        ViewModelProviders.of(this).get(TrainingListViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Total trainings: ${trainingListViewModel.trainings.size}")
    }

    companion object {
        fun newInstance(): TrainingListFragment {
            return TrainingListFragment()
        }
    }
}