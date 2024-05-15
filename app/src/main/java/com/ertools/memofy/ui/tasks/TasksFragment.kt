package com.ertools.memofy.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.R
import com.ertools.memofy.databinding.FragmentTasksBinding
import com.ertools.memofy.model.MemofyApplication
import kotlinx.coroutines.flow.collect

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        binding.tasksRecycler.layoutManager = LinearLayoutManager(requireContext())

        val taskRepository = (requireContext().applicationContext as MemofyApplication).taskRepository
        val categoryRepository = (requireContext().applicationContext as MemofyApplication).categoryRepository
        val tasksViewModel = ViewModelProvider(
            this, TasksViewModelFactory(taskRepository, categoryRepository)
        )[TasksViewModel::class.java]

        configureTasksAdapter(tasksViewModel)
        configureAddTaskButton()
        return binding.root
    }

    private fun configureTasksAdapter(tasksViewModel: TasksViewModel) {
        tasksViewModel.tasks.observe(viewLifecycleOwner) {
            val tasksAdapter = TasksAdapter(
                requireContext(),
                it,
                tasksViewModel
            )
            binding.tasksRecycler.adapter = tasksAdapter
        }
    }

    private fun configureAddTaskButton() {
        binding.tasksAddButton.setOnClickListener {
            findNavController().navigate(R.id.action_nav_tasks_to_nav_task)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}