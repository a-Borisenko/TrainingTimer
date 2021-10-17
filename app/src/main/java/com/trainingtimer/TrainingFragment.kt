package com.trainingtimer

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import java.util.*

class TrainingFragment : Fragment() {

    private lateinit var training: Training
    private lateinit var titleField: EditText
    private lateinit var restButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        training = Training(UUID.randomUUID(), "", 0, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_training, container, false)
        titleField = view.findViewById(R.id.training_title) as EditText
        return view
    }

    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                //
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                training.title = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //
            }
        }

        titleField.addTextChangedListener(titleWatcher)
        restButton = view?.findViewById(R.id.training_done) as Button

        restButton.apply {
            text = training.title.toString()
            //isEnabled = false
        }
    }
}