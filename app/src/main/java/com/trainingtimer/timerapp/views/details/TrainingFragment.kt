package com.trainingtimer.timerapp.views.details

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingBinding
import com.trainingtimer.foundation.domain.Training
import com.trainingtimer.timerapp.utils.AlarmReceiver
import com.trainingtimer.timerapp.views.timepicker.TimePickerFragment
import java.util.Calendar


class TrainingFragment : Fragment(R.layout.fragment_training) {

    /*enum class TimerState {
        Stopped, Running
    }*/

    private val calendar = Calendar.getInstance()
    private var alarmMgr: AlarmManager? = null
//    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L
    private var trainingId = Training.UNDEFINED_ID
    private var trainNumber = 0
    private var alarmDateTime = Calendar.getInstance()

    private lateinit var alarmIntent: PendingIntent
    private lateinit var timer: CountDownTimer
    private lateinit var binding: FragmentTrainingBinding
    private lateinit var viewModel: TrainingViewModel

    //TODO #1: drafting changes before saving data

    //TODO #2: background countdown

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogFragmentSettings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingBinding.bind(view)
        viewModel = ViewModelProvider(this)[TrainingViewModel::class.java]
        setMenu()

        trainingId = requireArguments().getInt("id")
        launchMode(trainingId)
        addTextChangeListeners()
        observeViewModel()
        updateCountdownUI()
        trainNumber()
//        textWatcher()
        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("key1", "$alarmDateTime")
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        Log.d("TrainingFragment", "alarmIntent 1")
    }

    private fun trainNumber() {
        viewModel.getTrainNumber()
        viewModel.trainNumber.observe(viewLifecycleOwner) {
            trainNumber = it.last().id + 1
        }
    }

    private fun dialogFragmentSettings() {
        childFragmentManager.setFragmentResultListener(
            "key", this
        ) { _, bundle ->
            secondsRemaining = bundle.getLong("time")
            updateCountdownUI()
        }
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_training, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.save_btn -> {
                        when (requireArguments().getInt("id")) {
                            Training.UNDEFINED_ID -> addTraining()
                            else -> editTraining()
                        }
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /*private fun textWatcher() {
        binding.tilSets.addOnEditTextAttachedListener(object : TextWatcher,
            TextInputLayout.OnEditTextAttachedListener {
            override fun afterTextChanged(p0: Editable?) {
                if (p0.toString() != "") {
                    viewModel.draftTraining(
                        binding.etSets.text.toString().toInt(),
                        binding.etTitle.text.toString(),
                        binding.etTimes.text.toString(),
                        binding.viewTimer.text.toString()
                    )
                    Log.d("TrainingFragment", "draft saved!!!")
//                    binding.tilSets.hint = ""
//                    saveDraftHandler.removeCallbacksAndMessages(null)
//                    saveDraftHandler.postDelayed({ saveDraft(p0.toString()) }, 1000)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }
            override fun onEditTextAttached(textInputLayout: TextInputLayout) {
                //
            }
        })
    }*/

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

    private fun launchMode(id: Int) {
        binding.trainingBtn.setOnClickListener {
            alarmMgr?.setExact(
                AlarmManager.RTC_WAKEUP,
                secondsRemaining * 1000,
                alarmIntent
            )
            startTimer()
        }
        binding.viewTimer.setOnClickListener {
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }
        if (id != Training.UNDEFINED_ID) {
            viewModel.getTraining(trainingId)
            viewModel.trainingLD.observe(viewLifecycleOwner) {
                binding.etSets.setText(it?.sets.toString())
                binding.etTitle.setText(it?.title)
                binding.etTimes.setText(it?.times)
                binding.viewTimer.text = it?.rest
                trainingId = id
            }
        }
    }

    private fun addTraining() {
        trainingId = trainNumber
        with(binding) {
            tilSets.isVisible = false
            tilTitle.isVisible = false
            tilTimes.isVisible = false
            viewTimer.isVisible = false
            trainingBtn.isVisible = false
            progressBar.isVisible = true
        }
        hideKeyboard()
        viewModel.addTraining(
            binding.etSets.text?.toString()?.toInt(),
            binding.etTitle.text?.toString(),
            binding.etTimes.text?.toString(),
            binding.viewTimer.text?.toString(),
            trainingId = trainingId
        )
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager
        = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun editTraining() {
        with(binding) {
            tilSets.isVisible = false
            tilTitle.isVisible = false
            tilTimes.isVisible = false
            viewTimer.isVisible = false
            trainingBtn.isVisible = false
            progressBar.isVisible = true
        }
        hideKeyboard()
        viewModel.editTraining(
            binding.etSets.text?.toString()?.toInt(),
            binding.etTitle.text?.toString(),
            binding.etTimes.text?.toString(),
            binding.viewTimer.text?.toString(),
            trainingId = trainingId
        )
    }

    private fun addTextChangeListeners() {
        binding.etTimes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputTimes()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.etTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputTitle()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
        binding.etSets.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputSets()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun startTimer() {
//        timerState = TimerState.Running

        val min = (binding.viewTimer.text.split(":"))[0].toInt()
        val sec = (binding.viewTimer.text.split(":"))[1].toInt()
        alarmDate(min, sec)
        Log.d("TrainingFragment", "startTimer")

        secondsRemaining = (min * 60 + sec).toLong()
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {
//                timerState = TimerState.Stopped
                Toast.makeText(context, R.string.timer_done, Toast.LENGTH_SHORT).show()
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()

        alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("key2", "$alarmDateTime")
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        Log.d("TrainingFragment", "$alarmDateTime")
        Log.d("TrainingFragment", "alarmIntent 2")
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI() {
//        binding.viewTimer.text = timeStringFromLong(secondsRemaining * 1000)
        val minutesFinished = secondsRemaining / 60
        val secondsInMinuteFinished = secondsRemaining - minutesFinished * 60
        val secondsStr = secondsInMinuteFinished.toString()
        binding.viewTimer.text = "${
            if (minutesFinished.toString().length == 2) minutesFinished
            else "0$minutesFinished"
        }:${
            if (secondsStr.length == 2) secondsStr
            else "0$secondsStr"
        }"
    }

    private fun alarmDate(min: Int, sec: Int) {
        //TODO: set calendar date & time from now + time left
        var alarmYear = calendar.get(Calendar.YEAR)
        var alarmMonth = calendar.get(Calendar.MONTH)
        var alarmDate = calendar.get(Calendar.DAY_OF_MONTH)
        var alarmHour = calendar.get(Calendar.HOUR_OF_DAY)
        var alarmMin = calendar.get(Calendar.MINUTE) + min
        var alarmSec = calendar.get(Calendar.SECOND) + sec

        /*if (alarmSec >= 60) {
            alarmSec -= 60
            alarmMin++
        }
        if (alarmMin >= 60) {
            alarmMin -= 60
            alarmHour++
        }
        if (alarmHour >= 24) {
            alarmHour -= 24
            alarmDate++
        }*/

        alarmDateTime.set(Calendar.YEAR, alarmYear)
        alarmDateTime.set(Calendar.MONTH, alarmMonth)
        alarmDateTime.set(Calendar.DAY_OF_MONTH, alarmDate)
        alarmDateTime.set(Calendar.HOUR_OF_DAY, alarmHour)
        alarmDateTime.set(Calendar.MINUTE, alarmMin)
        alarmDateTime.set(Calendar.SECOND, alarmSec)
    }
}