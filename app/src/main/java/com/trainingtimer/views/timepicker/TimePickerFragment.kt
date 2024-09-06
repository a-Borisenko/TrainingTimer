package com.trainingtimer.views.timepicker

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.trainingtimer.databinding.TimePickerDialogBinding

class TimePickerFragment : DialogFragment() {

    private var _binding: TimePickerDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = TimePickerDialogBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.save.setOnClickListener {
            val time = calculateTimeInSeconds()
            setFragmentResult("key", bundleOf("time" to time))
            dismiss()
        }
    }

    private fun calculateTimeInSeconds(): Long {
        val minutes = binding.timePicker.getCurrentMinutes()
        val seconds = binding.timePicker.getCurrentSeconds()
        return (minutes * 60 + seconds).toLong()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}