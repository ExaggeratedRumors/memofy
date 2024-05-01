package com.ertools.memofy.ui.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.databinding.FragmentTaskListBinding

class TaskListFragment : Fragment() {
    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val taskListViewModel =
            ViewModelProvider(this)[TaskListViewModel::class.java]

        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        val root: View = binding.root

        recyclerView = binding.taskListRecycler
        recyclerView.layoutManager = LinearLayoutManager(context)
        val items = listOf("Element 1", "Element 2", "Element 3", "Element 4", "Element 5")
        val adapter = TaskListAdapter(items)
        recyclerView.adapter = adapter

        taskListViewModel.

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}