package com.trainingtimer.ui

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.trainingtimer.R
import com.trainingtimer.databinding.TimePickerDialogBinding

class TimePickerFragment : DialogFragment() {

    private lateinit var binding: TimePickerDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = TimePickerDialogBinding.inflate(LayoutInflater.from(context))
        Log.d("TimePickerFragment", "onCreateDialog ${binding.timePicker.getCurrentMinutes()}")
        val listener = DialogInterface.OnClickListener { _, _ ->
            val time = (binding.timePicker.getCurrentMinutes() * 60 + binding.timePicker.getCurrentSeconds()).toLong()
            Log.d("TimePickerFragment", "listener ${binding.timePicker.getCurrentMinutes()}")
            Toast.makeText(context, "$time", Toast.LENGTH_SHORT).show()
            parentFragmentManager.setFragmentResult("key", bundleOf("time" to time))
            dismiss()
        }
        return AlertDialog.Builder(requireContext())
            .setTitle("Задайте время")
            .setView(R.layout.time_picker_dialog)
            .setPositiveButton("save", listener)
            .create()
        /*binding = TimePickerDialogBinding.bind(requireView())
        return AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()*/
    }

    override fun onStart() {
        super.onStart()
        Log.d("TimePickerFragment", "onStart ${binding.timePicker.getCurrentMinutes()}")
    }

    override fun onResume() {
        super.onResume()
        Log.d("TimePickerFragment", "onResume ${binding.timePicker.getCurrentMinutes()}")
//        DialogInterface.BUTTON_POSITIVE
//        binding .save.setOnClickListener {
//            val time = (binding.timePicker.getCurrentMinutes() * 60 + binding.timePicker.getCurrentSeconds()).toLong()
//            setFragmentResult("key", bundleOf("time" to time))
//            dismiss()
//        }
    }
}