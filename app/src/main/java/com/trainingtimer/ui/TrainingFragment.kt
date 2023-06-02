package com.trainingtimer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
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
        viewModel = ViewModelProvider(this)[TrainingViewModel::class.java]
        launchRightMode(requireArguments().getInt("id"))
        observeViewModel()
        binding.viewTimer.setOnClickListener {
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }
        updateCountdownUI()
    }

    private fun observeViewModel() {
        viewModel.errorInputSets.observe(viewLifecycleOwner) {
            binding.tilSets.error = if (it) {
                getString(R.string.error_input_sets)
            } else {
                null
            }
        }
        viewModel.errorInputTitle.observe(viewLifecycleOwner) {
            binding.tilTitle.error = if (it) {
                getString(R.string.error_input_title)
            } else {
                null
            }
        }
        viewModel.errorInputTimes.observe(viewLifecycleOwner) {
            binding.tilTimes.error = if (it) {
                getString(R.string.error_input_times)
            } else {
                null
            }
        }
        viewModel.shouldCloseScreen.observe(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    private fun launchRightMode(res: Int) {
        when (res) {
            Training.UNDEFINED_ID -> launchAddMode()
            else -> launchEditMode()
        }
    }

    private fun launchAddMode() {
        binding.trainingBtn.text = getString(R.string.training_save)
        binding.trainingBtn.setOnClickListener {
            addTraining()
        }
    }

    private fun launchEditMode() {
        binding.trainingBtn.text = getString(R.string.training_done)
        viewModel.getTraining(trainingId)
        viewModel.training.observe(viewLifecycleOwner) {
            binding.etTimes.setText(it.times)
            binding.etTitle.setText(it.title)
            binding.etSets.setText(it.sets)
            binding.viewTimer.text = it.rest
        }
        binding.trainingBtn.setOnClickListener {
            startTimer()
            editTraining()
        }
    }

    private fun addTraining() {
        viewModel.addTraining(
            binding.etTimes.text?.toString(),
            binding.etTitle.text?.toString(),
            binding.etSets.text?.toString(),
            binding.viewTimer.text?.toString()
        )
    }

    private fun editTraining() {
        viewModel.editTraining(
            binding.etTimes.text?.toString(),
            binding.etTitle.text?.toString(),
            binding.etSets.text?.toString(),
            binding.viewTimer.text?.toString()
        )
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
}