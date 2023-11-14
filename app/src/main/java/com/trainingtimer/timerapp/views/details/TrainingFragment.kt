package com.trainingtimer.timerapp.views.details

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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
import com.trainingtimer.timerapp.views.timepicker.TimePickerFragment


class TrainingFragment : Fragment(R.layout.fragment_training) {

    private var trainingId = Training.UNDEFINED_ID
    private var trainNumber = 0
    private var progr = 100f

    private lateinit var binding: FragmentTrainingBinding
    private lateinit var viewModel: TrainingViewModel
    private lateinit var timeReceiver: BroadcastReceiver

    //bug: back to list while countdown running & return show start time before updating

    //need refactor (move to viewModel) + liveData for trainingFragment ui

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingBinding.bind(view)
        viewModel = ViewModelProvider(this)[TrainingViewModel::class.java]
        trainingId = requireArguments().getInt("id")

        setMenu()
        onClickListeners()
        setDialogFragmentListener()
        savedInstance(savedInstanceState)
        launchMode()
        registerReceiver()
        addTextChangeListeners()
        inputErrorsObserve()
        getNumberOfTrainings()
        timeObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().unregisterReceiver(timeReceiver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("sets", binding.etSets.text.toString())
        outState.putString("title", binding.etTitle.text.toString())
        outState.putString("times", binding.etTimes.text.toString())
    }

    private fun timeObservers() {
        var timing = 0L
        viewModel.secRemain.observe(viewLifecycleOwner) {
            val min = it / 60
            val sec = it % 60
            binding.viewTimer.text = "${"%02d".format(min)}:${"%02d".format(sec)}"
            timing = it
        }
        viewModel.progress.observe(viewLifecycleOwner) {
//            var time = getTime(binding.viewTimer.text.toString()).toInt()
            if (timing > 0) {
                binding.countdownBar.progress = it.toInt()
            } else {
                binding.countdownBar.progress = 0
            }
        }
    }

    private fun getNumberOfTrainings() {
        viewModel.getTrainingNumber()
        viewModel.trainNumber.observe(viewLifecycleOwner) {
            trainNumber = it.last().id + 1
        }
    }

    private fun setDialogFragmentListener() {
        childFragmentManager.setFragmentResultListener(
            "key", this
        ) { _, bundle ->
            viewModel.updateTime(bundle.getLong("time"))
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
                        when (trainingId) {
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

    private fun inputErrorsObserve() {
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

    private fun onClickListeners() {
        binding.trainingBtn.setOnClickListener {
            startTimer()
        }
        binding.viewTimer.setOnClickListener {
            TimePickerFragment().show(childFragmentManager, "timePicker")
        }
    }

    private fun savedInstance(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            binding.etSets.setText(savedInstanceState.getString("sets"))
            binding.etTitle.setText(savedInstanceState.getString("title"))
            binding.etTimes.setText(savedInstanceState.getString("times"))
//            binding.viewTimer.text = savedInstanceState.getString("rest")
        }
    }

    //viewModel 58:00; dataFlow 1:11:42; launchController 1:25:48
    private fun launchMode() {
        if (trainingId != Training.UNDEFINED_ID) {
            viewModel.getTraining(trainingId)
            viewModel.trainingLD.observe(viewLifecycleOwner) {
                binding.etSets.setText(it?.sets.toString())
                binding.etTitle.setText(it?.title)
                binding.etTimes.setText(it?.times)
//                binding.viewTimer.text = it?.rest
                if (!TimerService.isCounting) {
                    viewModel.updateTime(getTime(it?.rest.toString()))
                } else {
                    viewModel.updateTime(secIntent)
                }
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
    }

    private fun getTime(time: String): Long {
        val min = (time.split(":"))[0].toLong()
        val sec = (time.split(":"))[1].toLong()
        return (min * 60 + sec)
    }

    private fun startTimer() {
        isClickable(false)

        val intentService = Intent(context, TimerService::class.java)
        intentService.putExtra("TimeValue", getTime(binding.viewTimer.text.toString()))
        requireActivity().startService(intentService)
    }

    private fun registerReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction("Counter")

        timeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                secIntent = intent.getLongExtra("TimeRemaining", 0)
                progr = intent.getFloatExtra("Progress", 100f)
                if (secIntent > 0) {
                    viewModel.updateTime(secIntent)
                    viewModel.updateProgress(progr)
                } else {
                    viewModel.updateTime(0)
                    viewModel.updateProgress(0f)
                    isClickable(true)
                }
            }
        }
        requireActivity().registerReceiver(timeReceiver, intentFilter)
    }

    private fun isClickable(status: Boolean) {
        with(binding) {
            trainingBtn.isClickable = status
            viewTimer.isClickable = status
        }
    }

    companion object {
        private var secIntent = 0L
    }
}