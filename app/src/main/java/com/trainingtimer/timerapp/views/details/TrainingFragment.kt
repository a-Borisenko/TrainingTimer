package com.trainingtimer.timerapp.views.details

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingBinding
import com.trainingtimer.foundation.domain.Training
import com.trainingtimer.timerapp.views.timepicker.TimePickerFragment


class TrainingFragment : Fragment(R.layout.fragment_training) {

    /*enum class TimerState {
        Stopped, Running
    }*/

//    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L
    private var trainingId = Training.UNDEFINED_ID
    private var trainNumber = 0

    private lateinit var timer: CountDownTimer
    private lateinit var binding: FragmentTrainingBinding
    private lateinit var viewModel: TrainingViewModel

    //TODO #1: lost of data changes when rotation: 1) user content form; 2) onTypeListener; 3) content sending to database

    //TODO #2: rotation make countdown lost & crash app after time up

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

    //moved to viewModel
    private fun textWatcher() {
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

    private fun launchMode(id: Int) {
        binding.trainingBtn.setOnClickListener {
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

        secondsRemaining = (binding.viewTimer.text.split(":"))[0].toLong() * 60 +
                (binding.viewTimer.text.split(":"))[1].toLong()
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() {
                binding.viewTimer.text = getString(R.string.timer_done)
            }

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI() {
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
}