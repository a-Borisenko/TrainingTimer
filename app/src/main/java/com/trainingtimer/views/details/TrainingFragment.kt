package com.trainingtimer.views.details

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
import androidx.navigation.fragment.findNavController
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingBinding
import com.trainingtimer.utils.DataService.Companion.START
import com.trainingtimer.utils.collectInViewScope
import com.trainingtimer.utils.onChange
import com.trainingtimer.utils.timeLongToString
import com.trainingtimer.utils.timeStringToLong
import com.trainingtimer.views.timepicker.TimePickerFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingFragment : Fragment(R.layout.fragment_training) {

    private val viewModel: TrainingViewModel by viewModels()
    private var _binding: FragmentTrainingBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrainingBinding.bind(view)

        viewModel.startViewModel()

        setMenu()
        setListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // TODO #1: change to ViewModel
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.saveState(
            binding.etSets.text.toString(),
            binding.etTitle.text.toString(),
            binding.etTimes.text.toString()
        )
    }

    private fun observeViewModel() {
        viewModel.apply {
            sets.collectInViewScope(this@TrainingFragment) { binding.etSets.setText(it) }
            title.collectInViewScope(this@TrainingFragment) { binding.etTitle.setText(it) }
            times.collectInViewScope(this@TrainingFragment) { binding.etTimes.setText(it) }
            secRemain.collectInViewScope(this@TrainingFragment) {
                binding.viewTimer.text = timeLongToString(it)
            }
            progress.collectInViewScope(this@TrainingFragment) {
                binding.countdownBar.progress = it.toInt()
            }

            errorInputSets.collectInViewScope(this@TrainingFragment) {
                binding.tilSets.error = if (it) getString(R.string.error_input_sets) else null
            }
            errorInputTitle.collectInViewScope(this@TrainingFragment) {
                binding.tilTitle.error = if (it) getString(R.string.error_input_title) else null
            }
            errorInputTimes.collectInViewScope(this@TrainingFragment) {
                binding.tilTimes.error = if (it) getString(R.string.error_input_times) else null
            }
            shouldCloseScreen.collectInViewScope(this@TrainingFragment) {
                it?.let {
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
        binding.apply {
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

            etSets.onChange { viewModel.resetErrorInputSets() }
            etTitle.onChange { viewModel.resetErrorInputTitle() }
            etTimes.onChange { viewModel.resetErrorInputTimes() }
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