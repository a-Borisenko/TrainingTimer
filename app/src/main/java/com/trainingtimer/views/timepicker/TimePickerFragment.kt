package com.trainingtimer.views.timepicker

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
        binding = TimePickerDialogBinding.inflate(layoutInflater)
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onResume() {
        super.onResume()
        binding.save.setOnClickListener {
            val time = binding.timePicker.getTimeLong()
            setFragmentResult("key", bundleOf("time" to time))
            dismiss()
        }
    }
}