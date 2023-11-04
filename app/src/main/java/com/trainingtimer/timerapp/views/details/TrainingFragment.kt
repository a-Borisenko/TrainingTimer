package com.trainingtimer.timerapp.views.details

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingBinding
import com.trainingtimer.foundation.domain.Training
import com.trainingtimer.timerapp.views.timepicker.TimePickerFragment


class TrainingFragment : Fragment(R.layout.fragment_training) {

//    private val calendar = Calendar.getInstance()
//    private var alarmMgr: AlarmManager? = null

    private var trainingId = Training.UNDEFINED_ID
    private var trainNumber = 0
//    private var alarmDateTime = Calendar.getInstance()
    private var progr = 100f
//    private var step = 0f

//    private lateinit var alarmIntent: PendingIntent
//    private lateinit var timer: CountDownTimer
    private lateinit var binding: FragmentTrainingBinding
    private lateinit var viewModel: TrainingViewModel
    private lateinit var timeReceiver: BroadcastReceiver

    //bug: back to list while countdown running & return show start time before updating

    //need refactor (move to viewModel) + liveData for trainingFragment ui

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dialogFragmentSettings()
        fragmentBackPressedSettings()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingBinding.bind(view)
        viewModel = ViewModelProvider(this)[TrainingViewModel::class.java]
        setMenu()

        trainingId = requireArguments().getInt("id")
        launchMode(trainingId, savedInstanceState)
        registerReceiver()
        addTextChangeListeners()
        observeViewModel()
        trainingNumber()
//        alarmMgr = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        /*alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("key1", "$alarmDateTime")
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }*/
//        Log.d("TrainingFragment", "alarmIntent 1")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        LocalBroadcastManager.getInstance(requireActivity().applicationContext)
            .unregisterReceiver(timeReceiver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("sets", binding.etSets.text.toString())
        outState.putString("title", binding.etTitle.text.toString())
        outState.putString("times", binding.etTimes.text.toString())
        outState.putString("rest", binding.viewTimer.text.toString())
//        outState.putLong("startTime", secondsStart)
    }

    private fun trainingNumber() {
        viewModel.getTrainingNumber()
        viewModel.trainNumber.observe(viewLifecycleOwner) {
            trainNumber = it.last().id + 1
        }
    }

    private fun dialogFragmentSettings() {
        childFragmentManager.setFragmentResultListener(
            "key", this
        ) { _, bundle ->
            secondsRemaining = bundle.getLong("time")
//            secondsStart = secondsRemaining
//            progr = 100f
//            step = progr / (secondsRemaining.toFloat())
            updateCountdownUI()
            updateProgressBarUI()
        }
    }

    private fun fragmentBackPressedSettings() {
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(this, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    binding.countdownBar.progress = 0
                    findNavController().popBackStack()
                }
            })
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

    private fun launchMode(id: Int, savedInstanceState: Bundle?) {
        binding.trainingBtn.setOnClickListener {
            /*alarmMgr?.setExact(
                AlarmManager.RTC_WAKEUP,
                secondsRemaining * 1000,
                alarmIntent
            )*/
            startTimer()
        }
        binding.viewTimer.setOnClickListener {
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }

        if (savedInstanceState != null) {
            binding.etSets.setText(savedInstanceState.getString("sets"))
            binding.etTitle.setText(savedInstanceState.getString("title"))
            binding.etTimes.setText(savedInstanceState.getString("times"))
            binding.viewTimer.text = savedInstanceState.getString("rest")

//            secondsStart = savedInstanceState.getLong("startTime")
//            progr = ((secondsRemaining * 100) / secondsStart).toFloat()
            updateTimer()
        } else if (id != Training.UNDEFINED_ID) {
            updateProgressBarUI()
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
        hideView()
        viewModel.addTraining(
            binding.etSets.text?.toString(),
            binding.etTitle.text?.toString(),
            binding.etTimes.text?.toString(),
            binding.viewTimer.text?.toString(),
            trainingId = trainingId
        )
    }

    private fun editTraining() {
        hideView()
        viewModel.editTraining(
            binding.etSets.text?.toString(),
            binding.etTitle.text?.toString(),
            binding.etTimes.text?.toString(),
            binding.viewTimer.text?.toString(),
            trainingId = trainingId
        )
    }

    private fun hideView() {
        viewModel.loading.observe(viewLifecycleOwner) {
            if (it) {
                with(binding) {
                    tilSets.isVisible = false
                    tilTitle.isVisible = false
                    tilTimes.isVisible = false
                    viewTimer.isVisible = false
                    trainingBtn.isVisible = false
                    countdownBar.isVisible = false
                    progressBar.isVisible = true
                }
                requireActivity().hideKeyboard(requireView())
            }
        }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager
                = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun EditText.textChangedListener(resFun: Unit) {
        val editText = EditText(context)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {resFun}
            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    private fun addTextChangeListeners() {
        binding.etSets.textChangedListener(viewModel.resetErrorInputSets())
        binding.etTitle.textChangedListener(viewModel.resetErrorInputTitle())
        binding.etTimes.textChangedListener(viewModel.resetErrorInputTimes())
        /*binding.etSets.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputSets()
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
        binding.etTimes.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.resetErrorInputTimes()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })*/
    }

    private fun startTimer() {
        val min = (binding.viewTimer.text.split(":"))[0].toLong()
        val sec = (binding.viewTimer.text.split(":"))[1].toLong()
//        alarmDate(min, sec)

        secondsRemaining = (min * 60 + sec)
//        if (secondsStart == 0L) secondsStart = secondsRemaining
        isClickable(false)

        val intentService = Intent(context, TimerService::class.java)
        intentService.putExtra("TimeValue", secondsRemaining)
        requireActivity().startService(intentService)

        /*timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {
                isClickable()
                updateProgressBarUI()
                Log.d("TrainingFragment", "done!!!")
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
                updateProgressBarUI()
            }
        }.start()*/

        /*alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
            intent.putExtra("key2", "$alarmDateTime")
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        Log.d("TrainingFragment", "$alarmDateTime")
        Log.d("TrainingFragment", "alarmIntent 2")*/
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("Counter")

        timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val secIntent = intent.getLongExtra("TimeRemaining", 0)
                progr = intent.getFloatExtra("Progress", 100f)
                if (secIntent > 0) {
                    secondsRemaining = secIntent
                } else {
                    secondsRemaining = 0
                    isClickable(true)
                }
                updateCountdownUI()
                updateProgressBarUI()
            }
        }
        requireActivity().registerReceiver(timeReceiver, intentFilter)
    }

    private fun updateTimer() {
        val min = (binding.viewTimer.text.split(":"))[0].toLong()
        val sec = (binding.viewTimer.text.split(":"))[1].toLong()

        secondsRemaining = (min * 60 + sec)
//        if (secondsStart == 0L) secondsStart = secondsRemaining
        isClickable(false)
    }

    private fun isClickable(status: Boolean) {
        if (secondsRemaining >= 0) {
            with(binding) {
                trainingBtn.isClickable = status
                viewTimer.isClickable = status
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI() {
        val min = secondsRemaining / 60
        val sec = secondsRemaining % 60
        binding.viewTimer.text = "${"%02d".format(min)}:${"%02d".format(sec)}"

        /*val minutesFinished = secondsRemaining / 60
        val secondsInMinuteFinished = secondsRemaining - minutesFinished * 60
        val secondsStr = secondsInMinuteFinished.toString()

        binding.viewTimer.text = "${
            if (minutesFinished.toString().length == 2) minutesFinished
            else "0$minutesFinished"
        }:${
            if (secondsStr.length == 2) secondsStr
            else "0$secondsStr"
        }"*/
        Log.d("countdown", "sec remain = $secondsRemaining")

        val newMin = (binding.viewTimer.text.split(":"))[0].toLong()
        val newSec = (binding.viewTimer.text.split(":"))[1].toLong()

        if ((newSec + newMin) != secondsRemaining) {
            binding.viewTimer.text = "${"%02d".format(min)}:${"%02d".format(sec)}"
        }
    }

    private fun updateProgressBarUI() {
        binding.countdownBar.progress = progr.toInt()
        /*if (step == 0f && secondsRemaining > 0) {
            step = progr / (secondsRemaining.toFloat())
        }
        progr -= step*/
    }

    /*private fun alarmDate(min: Int, sec: Int) {
        val alarmDate = calendar.get(Calendar.DAY_OF_MONTH)
        val alarmHour = calendar.get(Calendar.HOUR_OF_DAY)
        val alarmMin = calendar.get(Calendar.MINUTE) + min
        val alarmSec = calendar.get(Calendar.SECOND) + sec

        alarmDateTime.set(Calendar.DAY_OF_MONTH, alarmDate)
        alarmDateTime.set(Calendar.HOUR_OF_DAY, alarmHour)
        alarmDateTime.set(Calendar.MINUTE, alarmMin)
        alarmDateTime.set(Calendar.SECOND, alarmSec)
    }*/

    /*private val alarmInfoPendingIntent: PendingIntent
        get() {
            val alarmInfoIntent = Intent(context, TrainingFragment::class.java)
            alarmInfoIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            return PendingIntent.getActivity(
                context,
                0,
                alarmInfoIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    private val alarmActionPendingIntent: PendingIntent
        get() {
            val intent = Intent(context, TrainingFragment::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            return PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }*/

    companion object {
//        private var secondsStart = 0L
        private var secondsRemaining = 0L
    }
}