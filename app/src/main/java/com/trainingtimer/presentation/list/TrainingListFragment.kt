package com.trainingtimer.presentation.list

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.R.anim
import com.trainingtimer.R
import com.trainingtimer.databinding.FragmentTrainingListBinding
import com.trainingtimer.domain.entity.Training
import com.trainingtimer.utils.gone
import com.trainingtimer.utils.visible
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrainingListFragment : Fragment(R.layout.fragment_training_list) {

    private val viewModel: TrainingListViewModel by viewModels()
    private lateinit var listAdapter: TrainingAdapter
    private lateinit var binding: FragmentTrainingListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentTrainingListBinding.bind(view)
        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.trainingList.collect { trainings ->
                listAdapter.submitList(trainings)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            uiState()
        }
    }

    private suspend fun uiState() {
        viewModel.uiState.collect { state ->
            when (state) {
                TrainingUiState.Loading -> {
                    binding.progressBar.visible()
                    binding.trainingRecyclerView.gone()
                    binding.newTraining.gone()
                }
                TrainingUiState.Loaded -> {
                    binding.progressBar.gone()
                    binding.trainingRecyclerView.visible()
                    binding.newTraining.visible()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        val rvTrainingList = binding.trainingRecyclerView
        listAdapter = TrainingAdapter()
        rvTrainingList.adapter = listAdapter
        setupClickListener()
        setupSwipeListener(rvTrainingList)
    }

    private fun setupSwipeListener(rvTrainingList: RecyclerView) {
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
        itemTouchHelper.attachToRecyclerView(rvTrainingList)
    }

    private fun setupClickListener() {
        binding.newTraining.setOnLongClickListener { navCal() }
        listAdapter.onTrainingClickListener = { navigate(it.id) }

        binding.newTraining.setOnClickListener {
            navigate(Training.UNDEFINED_ID)
        }
    }

    private fun navCal(): Boolean {
        findNavController().navigate(
            R.id.action_trainingListFragment_to_calendar
        )
        return false
    }

    private fun navigate(id: Int) {
        findNavController().navigate(
            R.id.action_trainingListFragment_to_trainingFragment,
            bundleOf("id" to id),
            navigationAnimation()
        )
    }

    private fun navigationAnimation(): NavOptions {
        return navOptions {
            anim {
                enter = anim.abc_slide_in_bottom
                exit = anim.abc_slide_out_top
                popEnter = anim.abc_slide_in_top
                popExit = anim.abc_slide_out_bottom
            }
        }
    }
}