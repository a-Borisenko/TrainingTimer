package com.trainingtimer.timerapp.views.details

import android.app.Activity
import android.content.Context
import android.content.Intent
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
import com.trainingtimer.timerapp.views.details.TrainingUtils.Companion.timeLongToString
import com.trainingtimer.timerapp.views.details.TrainingUtils.Companion.timeStringToLong
import com.trainingtimer.timerapp.views.timepicker.TimePickerFragment


class TrainingFragment : Fragment(R.layout.fragment_training) {

    private var trainingId = Training.UNDEFINED_ID

    private lateinit var binding: FragmentTrainingBinding
    private lateinit var viewModel: TrainingViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingBinding.bind(view)
        viewModel = ViewModelProvider(this)[TrainingViewModel::class.java]
        trainingId = requireArguments().getInt("id")
        viewModel.start(trainingId)

        setMenu()
        onClickListeners()
        setDialogFragmentListener()
        addTextChangeListeners()
        inputErrorsObserve()
        timeObservers()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(
            binding.etSets.text.toString(),
            binding.etTitle.text.toString(),
            binding.etTimes.text.toString()
        )
    }

    private fun timeObservers() {
        with(viewModel) {
            sets.observe(viewLifecycleOwner) {
                binding.etSets.setText(it.toString())
            }
            title.observe(viewLifecycleOwner) {
                binding.etTitle.setText(it)
            }
            times.observe(viewLifecycleOwner) {
                binding.etTimes.setText(it)
            }
            secRemain.observe(viewLifecycleOwner) {
                binding.viewTimer.text = timeLongToString(it)
            }
            progress.observe(viewLifecycleOwner) {
                binding.countdownBar.progress = it.toInt()
            }
        }
    }

    private fun setDialogFragmentListener() {
        childFragmentManager.setFragmentResultListener(
            "key", this
        ) { _, bundle ->
            viewModel.updateTime(bundle.getLong("time"))
            viewModel.resetProgress()
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
                        trainingClickData()
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

    //viewModel 58:00; dataFlow 1:11:42; launchController 1:25:48

    private fun trainingClickData() {
        hideView()
        viewModel.trainingClickData(
            binding.etSets.text?.toString(),
            binding.etTitle.text?.toString(),
            binding.etTimes.text?.toString(),
            binding.viewTimer.text?.toString(),
            trainingId = trainingId
        )
    }

    //TODO #1: move to List (start mode)
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

    //TODO #2: no need, suspending move to List
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

    private fun startTimer() {
//        isClickable(false)

        val intentService = Intent(context, TimerService::class.java)
        intentService.putExtra("TimeValue", timeStringToLong(binding.viewTimer.text.toString()))
        requireActivity().startService(intentService)
    }

    private fun isClickable(status: Boolean) {
        binding.trainingBtn.isClickable = status
        binding.viewTimer.isClickable = status
    }
}