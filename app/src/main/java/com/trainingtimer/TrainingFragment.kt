package com.trainingtimer

import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.trainingtimer.databinding.FragmentTrainingBinding
import java.time.Clock
import java.util.*

private const val TAG = "TrainingFragment"
private const val ARG_TRAINING_ID = "training_id"

class TrainingFragment : Fragment() {

    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    private lateinit var training: Training
    private lateinit var titleField: EditText
    private val trainingDetailViewModel: TrainingDetailViewModel by lazy {
        ViewModelProviders.of(this).get(TrainingDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        training = Training(/*UUID.randomUUID(), "", 0, 0*/)
        val trainingId: UUID = arguments?.getSerializable(ARG_TRAINING_ID) as UUID
        //Log.d(TAG, "args bundle training ID: $trainingId")
        trainingDetailViewModel.loadTraining(trainingId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_training, container, false)
        titleField = view.findViewById(R.id.training_title) as EditText
        //return view
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trainingDone.setOnClickListener { view ->
            startTimer()
            timerState = TimerState.Running
        }
        trainingDetailViewModel.trainingLiveData.observe(
            viewLifecycleOwner,
            Observer { training ->
                training?.let {
                    this.training = training
                    updateUI()
                }
            }
        )
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                training.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }
        titleField.addTextChangedListener(titleWatcher)
    }

    override fun onStop() {
        super.onStop()
        trainingDetailViewModel.saveTraining(training)
    }

    private fun updateUI() {
        titleField.setText(training.title)
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped

        //set the length of the timer to be the one set in SettingsActivity
        //if the length was changed when the timer was running
        /*setNewTimerLength()

        progress_countdown.progress = 0

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()*/
    }

    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
            }
        }.start()
    }

    companion object{
        fun newInstance(trainingId: UUID): TrainingFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TRAINING_ID, trainingId)
            }
            return TrainingFragment().apply {
                arguments = args
            }
        }
    }
}