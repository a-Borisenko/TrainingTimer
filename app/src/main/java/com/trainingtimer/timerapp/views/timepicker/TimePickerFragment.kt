package com.trainingtimer.timerapp.views.timepicker

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.trainingtimer.databinding.TimePickerDialogBinding

class TimePickerFragment : DialogFragment() {

    private lateinit var binding: TimePickerDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TimePickerDialogBinding.inflate(LayoutInflater.from(context))
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()
    }

    override fun onResume() {
        super.onResume()
        binding.save.setOnClickListener {
            val time = (binding.timePicker.getCurrentMinutes() * 60
                    + binding.timePicker.getCurrentSeconds()).toLong()
            setFragmentResult("key", bundleOf("time" to time))
            dismiss()
        }
    }
}