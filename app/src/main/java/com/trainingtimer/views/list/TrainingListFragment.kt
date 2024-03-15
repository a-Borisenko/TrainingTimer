package com.trainingtimer.views.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingListFragment : Fragment(R.layout.fragment_training_list) {

    private lateinit var viewModel: TrainingListViewModel
    private lateinit var listAdapter: TrainingAdapter
    private lateinit var binding: FragmentTrainingListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[TrainingListViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingListBinding.bind(view)
        setupRecyclerView()
        viewModel.trainingList.observe(viewLifecycleOwner) {
            listAdapter.submitList(it)
        }
        with(binding) {
            progressBar.isVisible = false
            trainingRecyclerView.isVisible = true
            newTraining.isVisible = true
        }
    }

    private fun setupRecyclerView() {
        val rvTrainingList = binding.trainingRecyclerView
        listAdapter = TrainingAdapter()
        rvTrainingList.adapter = listAdapter
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
                val item = listAdapter.currentList[viewHolder.bindingAdapterPosition]
                viewModel.deleteTraining(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvShopList)
    }

    private fun setupClickListener() {
        listAdapter.onTrainingClickListener = {
            navigate(it.id)
        }
        binding.newTraining.setOnClickListener {
            navigate(UNDEFINED_ID)
        }
    }

    private fun navigate(id: Int) {
        findNavController().navigate(
            R.id.action_trainingListFragment_to_trainingFragment,
            bundleOf("id" to id),
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