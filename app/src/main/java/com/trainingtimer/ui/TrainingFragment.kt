package com.trainingtimer.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingBinding

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
//    private var _binding: FragmentTrainingBinding? = null
//    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(
            "key", this
        ) { _, bundle ->
            secondsRemaining = bundle.getLong("time")
            updateCountdownUI()
        }
    }

    /*override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
        return binding.root
    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trainingDone.setOnClickListener {
            startTimer()
        }
        binding.viewTimer.setOnClickListener {
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }
        updateCountdownUI()
    }

    /*override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }*/

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

    /*TODO: for future trainingList recycler
    companion object {
        fun newInstance(trainingId: UUID): TrainingFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TRAINING_ID, trainingId)
            }
            return TrainingFragment().apply {
                arguments = args
            }
        }
    }*/
}