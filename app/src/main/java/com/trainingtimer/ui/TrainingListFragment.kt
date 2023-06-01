package com.trainingtimer.ui

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R.anim
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingListBinding
import com.trainingtimer.domain.Training.Companion.UNDEFINED_ID

private const val TAG = "TrainingListFragment"

class TrainingListFragment : Fragment(R.layout.fragment_training_list) {

    private lateinit var trainingListAdapter: TrainingAdapter
    private lateinit var trainingListViewModel: TrainingListViewModel
    private lateinit var binding: FragmentTrainingListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trainingListViewModel = ViewModelProvider(this)[TrainingListViewModel::class.java]
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_training_list, menu)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingListBinding.bind(view)
        setupRecyclerView()
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
            Log.d("ListFrag", "item with id ${it.id} clicked!")
            findNavController().navigate(
                R.id.action_trainingListFragment_to_trainingFragment,
                bundleOf("id" to it.id),
                navOptions {
                    anim {
                        enter = anim.abc_popup_enter
                        exit = anim.abc_popup_enter
                        popEnter = anim.abc_popup_enter
                        popExit = anim.abc_popup_enter
                    }
                }
            )
        }
        binding.newTraining.setOnClickListener {
            findNavController().navigate(
                R.id.action_trainingListFragment_to_trainingFragment,
                bundleOf("id" to UNDEFINED_ID),
                navOptions {
                    anim {
                        enter = anim.abc_popup_enter
                        exit = anim.abc_popup_enter
                        popEnter = anim.abc_popup_enter
                        popExit = anim.abc_popup_enter
                    }
                }
            )
        }
    }
}