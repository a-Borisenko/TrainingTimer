package com.trainingtimer

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*

private const val TAG = "TrainingListFragment"

class TrainingListFragment : Fragment() {

    interface Callbacks {
        fun onTrainingSelected(trainingId: UUID)
    }

    private var callbacks: Callbacks? = null
    private lateinit var trainingRecyclerView: RecyclerView
    private var adapter: TrainingAdapter? = TrainingAdapter(emptyList())
    private val trainingListViewModel: TrainingListViewModel by lazy {
        ViewModelProvider(this).get(TrainingListViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.d(TAG, "Total trainings: ${trainingListViewModel.trainings.size}")
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragment_training_list, container, false)
        trainingRecyclerView =
            view.findViewById(R.id.training_recycler_view) as RecyclerView
        trainingRecyclerView.layoutManager = LinearLayoutManager(context)
        //updateUI()
        trainingRecyclerView.adapter = adapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trainingListViewModel.trainingListLiveData.observe(viewLifecycleOwner) { trainings ->
            trainings?.let {
                Log.i(TAG, "Got trainings ${trainings.size}")
                updateUI(trainings)
            }
        }
    }

    override fun onDetach() {
        super.onDetach()
        callbacks = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_training_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.add_training -> {
                val training = Training()
                trainingListViewModel.addTraining(training)
                callbacks?.onTrainingSelected(training.id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun updateUI(trainings: List<Training>) {
        //val trainings = trainingListViewModel.trainings
        adapter = TrainingAdapter(trainings)
        trainingRecyclerView.adapter = adapter
    }

    private inner class TrainingHolder(view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var training: Training
        private val titleTextView: TextView = itemView.findViewById(R.id.training_title)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(training: Training) {
            this.training = training
            titleTextView.text = this.training.title
        }

        override fun onClick(v: View) {
            Toast.makeText(context, "${training.title} pressed!", Toast.LENGTH_SHORT)
                .show()
            callbacks?.onTrainingSelected(training.id)
        }
    }

    private inner class TrainingAdapter(var trainings: List<Training>) :
        RecyclerView.Adapter<TrainingHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
                : TrainingHolder {
            val view = layoutInflater.inflate(R.layout.list_item_training, parent, false)
            return TrainingHolder(view)
        }

        override fun onBindViewHolder(holder: TrainingHolder, position: Int) {
            val training = trainings[position]
            holder.bind(training)
        }

        override fun getItemCount() = trainings.size
    }

    /*companion object {
        fun newInstance(): TrainingListFragment {
            return TrainingListFragment()
        }
    }*/
}