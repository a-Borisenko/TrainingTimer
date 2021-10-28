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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.*

private const val TAG = "TrainingFragment"
private const val ARG_TRAINING_ID = "training_id"

class TrainingFragment : Fragment() {

    private lateinit var training: Training
    private lateinit var titleField: EditText
    private lateinit var restButton: Button
    private val trainingDetailViewModel: TrainingDetailViewModel by lazy {
        ViewModelProviders.of(this).get(TrainingDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        training = Training(UUID.randomUUID(), "", 0, 0)
        val trainingId: UUID = arguments?.getSerializable(ARG_TRAINING_ID) as UUID
        //Log.d(TAG, "args bundle training ID: $trainingId")
        trainingDetailViewModel.loadTraining(trainingId)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trainingDetailViewModel.trainingLiveData.observe(
            viewLifecycleOwner,
            Observer { training ->
                training?.let {
                    this.training = training
                    updateUI()
                }
            }
        )
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
        }
    }

    override fun onStop() {
        super.onStop()
        trainingDetailViewModel.saveTraining(training)
    }

    private fun updateUI() {
        titleField.setText(training.title)
    }

    companion object{
        fun newInstance(trainingId: UUID): TrainingFragment {
            val args = Bundle().apply {
                putSerializable(ARG_TRAINING_ID, trainingId)
            }
            return TrainingFragment().apply {
                arguments = args
            }
        }
    }
}