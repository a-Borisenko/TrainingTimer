package com.trainingtimer.presentation.details

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingBinding
import com.trainingtimer.utils.DataService.Companion.START
import com.trainingtimer.utils.onChange
import com.trainingtimer.utils.timeLongToString
import com.trainingtimer.utils.timeStringToLong
import com.trainingtimer.presentation.timepicker.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingFragment : Fragment(R.layout.fragment_training) {

    private val viewModel: TrainingViewModel by viewModels()
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrainingBinding.bind(view)

        setMenu()
        setListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.state.collect { state ->
                with(binding) {
                    etSets.setText(state.sets)
                    etTitle.setText(state.title)
                    etTimes.setText(state.times)
                    viewTimer.text = timeLongToString(state.secRemain)
                    countdownBar.progress = state.progress.toInt()

                    tilSets.error =
                        if (state.errorInputSets) getString(R.string.error_input_sets) else null
                    tilTitle.error =
                        if (state.errorInputTitle) getString(R.string.error_input_title) else null
                    tilTimes.error =
                        if (state.errorInputTimes) getString(R.string.error_input_times) else null
                }

                if (state.shouldCloseScreen) {
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.fragment_training, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when {
                    !TimerService.isCounting && menuItem.itemId == R.id.save_btn -> {
                        trainingClickData()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setListeners() {
        with(binding) {
            trainingBtn.setOnClickListener {
                if (!TimerService.isCounting) {
                    ContextCompat.startForegroundService(
                        requireContext(),
                        TimerService.newIntent(requireContext(), START)
                    )
                    viewModel.startTimer(timeStringToLong(viewTimer.text.toString()))
                }
            }

            viewTimer.setOnClickListener {
                if (!TimerService.isCounting) {
                    TimePickerFragment().show(childFragmentManager, "timePicker")
                }
            }

            etSets.onChange { viewModel.resetErrorInputSets(etSets.text.toString()) }
            etTitle.onChange { viewModel.resetErrorInputTitle(etTitle.text.toString()) }
            etTimes.onChange { viewModel.resetErrorInputTimes(etTimes.text.toString()) }
        }

        childFragmentManager.setFragmentResultListener("key", this) { _, bundle ->
            viewModel.updateTime(bundle.getLong("time"))
        }
    }

    private fun trainingClickData() {
        if (!TimerService.isCounting) {
            viewModel.trainingClickData(
                binding.etSets.text?.toString(),
                binding.etTitle.text?.toString(),
                binding.etTimes.text?.toString(),
                binding.viewTimer.text?.toString()
            )
        }
    }
}