package com.trainingtimer.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingListBinding

private const val TAG = "TrainingListFragment"

class TrainingListFragment : Fragment() {

    /*interface Callbacks {
        fun onTrainingSelected(trainingId: Int)    //UUID
    }*/

//    private var callbacks: Callbacks? = null
    private lateinit var trainingRecyclerView: RecyclerView

//    private var adapter: TrainingAdapter? = TrainingAdapter(emptyList())
    private lateinit var trainingListAdapter: TrainingAdapter
    private lateinit var trainingListViewModel: TrainingListViewModel /*by lazy {
        ViewModelProvider(this)[TrainingListViewModel::class.java]
    }*/

    private var _binding: FragmentTrainingListBinding? = null
    private val binding get() = _binding!!

    /*override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callbacks?
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trainingListViewModel = ViewModelProvider(this)[TrainingListViewModel::class.java]
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingListBinding.inflate(inflater, container, false)
        /*val view = inflater.inflate(R.layout.fragment_training_list, container, false)
        trainingRecyclerView = view.findViewById(R.id.training_recycler_view) as RecyclerView
        trainingRecyclerView.layoutManager = LinearLayoutManager(context)
        trainingRecyclerView.adapter = trainingListAdapter
        return view*/
        setupRecyclerView()
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_training_list, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*trainingListViewModel.trainingListLiveData.observe(viewLifecycleOwner) { trainings ->
            trainings?.let {
                Log.i(TAG, "Got trainings ${trainings.size}")
                updateUI()
            }
        }*/
        trainingListViewModel.trainingList.observe(viewLifecycleOwner) {
            trainingListAdapter.submitList(it)
        }
    }

    /*override fun onDetach() {
        super.onDetach()
        callbacks = null
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

    /*private fun updateUI() {
        trainingListAdapter = TrainingAdapter()
        trainingRecyclerView.adapter = trainingListAdapter
    }*/

    private fun setupRecyclerView() {
        val rvTrainingList = binding.trainingRecyclerView
        trainingListAdapter = TrainingAdapter()
        rvTrainingList.adapter = trainingListAdapter
        setupClickListener()
        setupSwipeListener(rvTrainingList)
    }

    private fun setupSwipeListener(rvShopList: RecyclerView) {
        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val item = trainingListAdapter.currentList[viewHolder.adapterPosition]
                trainingListViewModel.deleteTraining(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun setupClickListener() {
        trainingListAdapter.onShopItemClickListener = {
            Log.d("MainActivity", "item with id ${it.id} clicked!")
            findNavController().navigate(
                R.id.action_trainingListFragment_to_trainingFragment,
                bundleOf("id" to it.id),
                navOptions {
                    anim {
                        enter = com.google.android.material.R.anim.abc_popup_enter
                        exit = com.google.android.material.R.anim.abc_popup_enter
                        popEnter = com.google.android.material.R.anim.abc_popup_enter
                        popExit = com.google.android.material.R.anim.abc_popup_enter
                    }
                }
            )
        }
    }

    /*companion object {
        fun newInstance(): TrainingListFragment {
            return TrainingListFragment()
        }
    }*/
}