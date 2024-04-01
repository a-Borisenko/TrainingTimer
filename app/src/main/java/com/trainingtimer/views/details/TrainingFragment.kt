package com.trainingtimer.views.details

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingBinding
import com.trainingtimer.domain.Training
import com.trainingtimer.views.timepicker.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class TrainingFragment : Fragment(R.layout.fragment_training) {

    private var trainingId = Training.UNDEFINED_ID
    private val viewModel: TrainingViewModel by viewModels()

    private lateinit var binding: FragmentTrainingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingBinding.bind(view)
        //TODO #2: move to ViewModel
        trainingId = if (TimerService.isCounting) {
            TimerService.currentId
        } else {
            requireArguments().getInt("id")
        }
        viewModel.start(trainingId)

        setMenu()
        onClickListeners()
        setDialogFragmentListener()
        addTextChangeListeners()
        inputErrorsObserve()
        dataObservers()
    }

    // change to ViewModel
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(
            binding.etSets.text.toString(),
            binding.etTitle.text.toString(),
            binding.etTimes.text.toString()
        )
    }

    private fun dataObservers() {
        with(viewModel) {
            sets
                .onEach {
                    binding.etSets.setText(it)
                }.launchWhenStarted(lifecycleScope)
            title
                .onEach {
                    binding.etTitle.setText(it)
                }.launchWhenStarted(lifecycleScope)
            times
                .onEach {
                    binding.etTimes.setText(it)
                }.launchWhenStarted(lifecycleScope)
            secRemain
                .onEach {
                    binding.viewTimer.text = timeLongToString(it)
                }.launchWhenStarted(lifecycleScope)
            progress
                .onEach {
                    binding.countdownBar.progress = it.toInt()
                }.launchWhenStarted(lifecycleScope)
        }
    }

    private fun setDialogFragmentListener() {
        childFragmentManager.setFragmentResultListener(
            "key", this
        ) { _, bundle ->
            viewModel.updateTime(bundle.getLong("time"))
            viewModel.resetProgress(trainingId)
        }
    }

    private fun setMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_training, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return if (!TimerService.isCounting) {
                    when (menuItem.itemId) {
                        R.id.save_btn -> {
                            trainingClickData()
                            true
                        }
                        else -> false
                    }
                } else false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun inputErrorsObserve() {
        with(viewModel) {
            errorInputSets
                .onEach {
                    binding.tilSets.error = if (it) {
                        getString(R.string.error_input_sets)
                    } else {
                        null
                    }
                }.launchWhenStarted(lifecycleScope)
            errorInputTitle
                .onEach {
                    binding.tilTitle.error = if (it) {
                        getString(R.string.error_input_title)
                    } else {
                        null
                    }
                }.launchWhenStarted(lifecycleScope)
            errorInputTimes
                .onEach {
                    binding.tilTimes.error = if (it) {
                        getString(R.string.error_input_times)
                    } else {
                        null
                    }
                }.launchWhenStarted(lifecycleScope)
            shouldCloseScreen.observe(viewLifecycleOwner) {
                findNavController().popBackStack()
            }
        }
    }

    private fun onClickListeners() {
        binding.trainingBtn.setOnClickListener {
            if (TimerService != null) {
                Log.d("trainingFragment", "TimerService exist")
                if (!TimerService.isCounting) {
                    startTimer()
                }
            } else {
                Log.d("trainingFragment", "no TimerService")
                startTimer()
            }
        }
        binding.viewTimer.setOnClickListener {
            if (!TimerService.isCounting) {
                TimePickerFragment().show(childFragmentManager, "timePicker")
            }
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
                    tilSets.hide()
                    tilTitle.hide()
                    tilTimes.hide()
                    viewTimer.hide()
                    trainingBtn.hide()
                    countdownBar.hide()
                    progressBar.show()
                }
                requireActivity().hideKeyboard(requireView())
            }
        }
    }

    private fun addTextChangeListeners() {
        binding.etSets.onChange {
            viewModel.resetErrorInputSets()
        }
        binding.etTitle.onChange {
            viewModel.resetErrorInputTitle()
        }
        binding.etTimes.onChange {
            viewModel.resetErrorInputTimes()
        }
    }

    //TODO #3: move to ViewModel
    private fun startTimer() {
        if (timeStringToLong(binding.viewTimer.text.toString()) > 0L) {
            val intentService = Intent(context, TimerService::class.java).apply {
                putExtra(TIME_VALUE, timeStringToLong(binding.viewTimer.text.toString()))
                putExtra(CURRENT_STATE, START)
                putExtra("id", trainingId)
            }
            requireActivity().startService(intentService)
        }
    }
}