package com.trainingtimer

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.trainingtimer.databinding.FragmentTrainingBinding
import java.util.*

private const val TAG = "TrainingFragment"
private const val ARG_TRAINING_ID = "training_id"

class TrainingFragment : Fragment() {

    enum class TimerState {
        Stopped, Running
    }

    private var timerState = TimerState.Stopped

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds = 0L
    private var secondsRemaining = 0L
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    private lateinit var training: Training
//    private lateinit var titleField: EditText
    private val trainingDetailViewModel: TrainingDetailViewModel by lazy {
        ViewModelProvider(this).get(TrainingDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        training = Training(/*UUID.randomUUID(), "", 0, 0*/)
        /*TODO: for future trainingList recycler
        val trainingId: UUID = UUID.randomUUID()
        //arguments?.getSerializable(ARG_TRAINING_ID) as UUID
        trainingDetailViewModel.loadTraining(trainingId)*/

        childFragmentManager.setFragmentResultListener(
            "key", this
        ) { _, bundle ->
            secondsRemaining = bundle.getLong("time")
            updateCountdownUI()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*TODO: for future trainingList recycler
        val view = inflater.inflate(R.layout.fragment_training, container, false)
        titleField = view.findViewById(R.id.training_title) as EditText*/
        _binding = FragmentTrainingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.trainingDone.setOnClickListener {
            startTimer()
            timerState = TimerState.Running
        }
        binding.viewTimer.setOnClickListener {
            timerState = TimerState.Stopped
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }
        //TODO: for future trainingList recycler
        /*trainingDetailViewModel.trainingLiveData.observe(viewLifecycleOwner) { training ->
            training?.let {
                this.training = training
                updateUI()
            }
        }*/
    }

    /*TODO: for future trainingList recycler
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
//        titleField.addTextChangedListener(titleWatcher)
    }*/

    override fun onResume() {
        super.onResume()
        initTimer()
        //TODO: remove background timer, hide notification
    }

    override fun onPause() {
        super.onPause()

        when (timerState) {
            TimerState.Running -> timer.cancel() //TODO: start background timer and show notification
            TimerState.Stopped -> TODO()
        }
        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, binding.root.context)
        PrefUtil.setSecondsRemaining(secondsRemaining, binding.root.context)
        PrefUtil.setTimerState(timerState, binding.root.context)
    }

    override fun onStop() {
        super.onStop()
        trainingDetailViewModel.saveTraining(training)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(binding.root.context)

        //we don't want to change the length of the timer which is already running
        //if the length was changed in settings while it was backgrounded
        when (timerState) {
            TimerState.Stopped -> setNewTimerLength()
            else -> setPreviousTimerLength()
        }

        //autostart counting down timer`s
        /*secondsRemaining = when (timerState) {
            TimerState.Running -> PrefUtil.getSecondsRemaining(binding.root.context)
            else -> timerLengthSeconds
        }*/
        //TODO: change secondsRemaining according to where the background timer stopped
        //resume where we left off
        if (timerState == TimerState.Running) startTimer()
        updateCountdownUI()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(binding.root.context)
        //progress_countdown.max = timerLengthSeconds.toInt()
    }

    /*TODO: for future trainingList recycler
    private fun updateUI() {
        titleField.setText(training.title)
    }*/

    private fun onTimerFinished() {
        timerState = TimerState.Stopped
        setNewTimerLength()
        //progress_countdown.progress = 0
        PrefUtil.setSecondsRemaining(timerLengthSeconds, binding.root.context)
        secondsRemaining = timerLengthSeconds
        updateCountdownUI()
        binding.viewTimer.text = "done!"
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
        //progress_countdown.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = 0 //PrefUtil.getTimerLength(this)//autoset app starting with 1 minute
        timerLengthSeconds = (lengthInMinutes * 60L)
        //progress_countdown.max = timerLengthSeconds.toInt()
    }

    private fun startTimer() {
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

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