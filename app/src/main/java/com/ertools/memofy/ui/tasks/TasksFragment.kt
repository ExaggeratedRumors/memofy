package com.ertools.memofy.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.R
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.databinding.FragmentTasksBinding
import com.ertools.memofy.model.MemofyApplication
import com.ertools.memofy.ui.task.TaskFragment

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
        val tasksViewModel = ViewModelProvider(
            this, TasksViewModelFactory(taskRepository)
        )[TasksViewModel::class.java]

        configureTasksAdapter(tasksViewModel)
        configureAddTaskButton(tasksViewModel)
        return binding.root
    }

    private fun configureTasksAdapter(tasksViewModel: TasksViewModel) {
        tasksViewModel.tasksList.observe(viewLifecycleOwner) {
            val tasksAdapter = TasksAdapter(requireContext(), it)
            binding.tasksRecycler.adapter = tasksAdapter
        }
    }

    private fun configureAddTaskButton(tasksViewModel: TasksViewModel) {
        binding.tasksAddButton.setOnClickListener {
            it.findNavController().navigate(R.id.nav_task)

            /*tasksViewModel.insertTask(
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
            )*/
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}