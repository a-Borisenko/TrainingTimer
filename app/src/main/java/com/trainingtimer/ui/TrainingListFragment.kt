package com.trainingtimer.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R

private const val TAG = "TrainingListFragment"

class TrainingListFragment : Fragment() {

    /*interface Callbacks {
        fun onTrainingSelected(trainingId: Int)    //UUID
    }*/

//    private var callbacks: Callbacks? = null
    private lateinit var trainingRecyclerView: RecyclerView

//    private var adapter: TrainingAdapter? = TrainingAdapter(emptyList())
    private lateinit var trainingListAdapter: TrainingAdapter
    private val trainingListViewModel: TrainingListViewModel by lazy {
        ViewModelProvider(this)[TrainingListViewModel::class.java]
    }

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_training_list, container, false)
        trainingRecyclerView = view.findViewById(R.id.training_recycler_view) as RecyclerView
        trainingRecyclerView.layoutManager = LinearLayoutManager(context)
        trainingRecyclerView.adapter = trainingListAdapter
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        trainingListViewModel.trainingListLiveData.observe(viewLifecycleOwner) { trainings ->
            trainings?.let {
                Log.i(TAG, "Got trainings ${trainings.size}")
                updateUI()
            }
        }
    }

    /*override fun onDetach() {
        super.onDetach()
        callbacks = null
    }*/

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_training_list, menu)
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.new_training -> {
                val training = Training("", "", "x", "00:00")
                trainingListViewModel.addTraining(training)
                callbacks?.onTrainingSelected(training.id)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    private fun updateUI() {
        trainingListAdapter = TrainingAdapter()
        trainingRecyclerView.adapter = trainingListAdapter
    }

    /*private inner class TrainingHolder(view: View) : RecyclerView.ViewHolder(view),
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
    }*/

    /*private inner class TrainingAdapter(var trainings: List<Training>) :
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
    }*/

    /*companion object {
        fun newInstance(): TrainingListFragment {
            return TrainingListFragment()
        }
    }*/
}