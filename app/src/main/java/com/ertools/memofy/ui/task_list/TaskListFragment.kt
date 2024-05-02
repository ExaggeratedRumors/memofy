package com.ertools.memofy.ui.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        binding.taskListRecycler.layoutManager = LinearLayoutManager(requireContext())

        val taskListViewModel = ViewModelProvider(this)[TaskListViewModel::class.java]
        taskListViewModel.tasksList.observe(viewLifecycleOwner) {
            val taskListAdapter = TaskListAdapter(requireContext(), it)
            binding.taskListRecycler.adapter = taskListAdapter
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}