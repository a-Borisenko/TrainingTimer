package com.trainingtimer.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.trainingtimer.databinding.TimePickerDialogBinding

class TimePickerFragment : DialogFragment() {

    private lateinit var binding: TimePickerDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TimePickerDialogBinding.bind(requireView())
        return AlertDialog.Builder(requireActivity())
            .setView(binding.root)
            .create()
    }

    override fun onResume() {
        super.onResume()
        binding.save.setOnClickListener {
            val result = (binding.timePicker.getCurrentMinutes() * 60
                    + binding.timePicker.getCurrentSeconds()).toLong()
            setFragmentResult("key", bundleOf("time" to result))
            dismiss()
        }
    }
}