package com.ertools.memofy.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.databinding.FragmentTasksBinding
import com.ertools.memofy.model.MemofyApplication

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
        val taskListViewModel = ViewModelProvider(
            this, TasksViewModelFactory(taskRepository)
        )[TasksViewModel::class.java]
        taskListViewModel.tasksList.observe(viewLifecycleOwner) {
            val tasksAdapter = TasksAdapter(requireContext(), it)
            binding.tasksRecycler.adapter = tasksAdapter
        }

        /** Button configuration **/
        binding.tasksAddButton.setOnClickListener {
            taskListViewModel.insertTask(
                Task(
                    null,
                    "Task 123",
                    "Description 1",
                    "null",
                    "null",
                    0,
                    false,
                    0,
                    "null"
                )
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}