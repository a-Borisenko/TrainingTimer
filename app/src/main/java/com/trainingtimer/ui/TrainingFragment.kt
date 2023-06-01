package com.trainingtimer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingBinding
import com.trainingtimer.domain.Training

private const val TAG = "TrainingFragment"
private const val ARG_TRAINING_ID = "training_id"

class TrainingFragment : Fragment(R.layout.fragment_training) {

    /*enum class TimerState {
        Stopped, Running
    }*/

//    private var timerState = TimerState.Stopped

    private lateinit var timer: CountDownTimer
//    private var timerLengthSeconds = 0L
    private var secondsRemaining = 0L
    private lateinit var binding: FragmentTrainingBinding
    private lateinit var viewModel: TrainingViewModel

    private var trainingId = Training.UNDEFINED_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(
            "key", this
        ) { _, bundle ->
            secondsRemaining = bundle.getLong("time")
            updateCountdownUI()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingBinding.bind(view)
        val res = requireArguments().getInt("id") //getting id for 'new' or 'edit' mode
        Toast.makeText(context, "$res", Toast.LENGTH_SHORT).show()
        launchRightMode(res)
        binding.trainingDone.setOnClickListener {
            startTimer()
        }
        binding.viewTimer.setOnClickListener {
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }
        updateCountdownUI()
    }

    private fun launchRightMode(res: Int) {
        when (res) {
            -1 -> launchEditMode()
            else -> launchAddMode()
        }
    }

    private fun launchEditMode() {
        viewModel.getTraining(trainingId)
        viewModel.training.observe(viewLifecycleOwner) {}
    }

    private fun launchAddMode() {}

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        binding.viewTimer.text = "${
            if (minutesUntilFinished.toString().length == 2) minutesUntilFinished
            else "0$minutesUntilFinished"
        }:${
            if (secondsStr.length == 2) secondsStr
            else "0$secondsStr"
        }"
    }

    private fun startTimer() {
//        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {
                binding.viewTimer.text = "done!"
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }
}