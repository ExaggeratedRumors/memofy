package com.ertools.memofy.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.databinding.FragmentTasksPageBinding
import com.ertools.memofy.utils.Utils

class TasksPageFragment : Fragment() {
    private var _binding: FragmentTasksPageBinding? = null
    private val binding get() = _binding!!
    private val tasksViewModel: TasksViewModel by activityViewModels()
    private var taskStatusCode: Int? = null

    companion object {
        fun newInstance(status: Int? = null): TasksPageFragment {
            val fragment = TasksPageFragment()
            if(status != null) {
                val args = Bundle()
                args.putInt(Utils.BUNDLE_STATUS_FILTER, status)
                fragment.arguments = args
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksPageBinding.inflate(inflater, container, false)
        binding.tasksRecycler.layoutManager = LinearLayoutManager(requireContext())

        arguments?.let {
            taskStatusCode = it.getInt(Utils.BUNDLE_STATUS_FILTER, 0)
        }

        configureTasksRecyclerAdapter()
        return binding.root
    }

    private fun configureTasksRecyclerAdapter() {
        val tasksRecyclerAdapter = TasksRecyclerAdapter(requireContext(), tasksViewModel)
        binding.tasksRecycler.adapter = tasksRecyclerAdapter

        tasksViewModel.getDataByStatus(taskStatusCode).observe(viewLifecycleOwner) {
            tasksRecyclerAdapter.submitTasks(it)
        }

        tasksViewModel.categories.observe(viewLifecycleOwner) {
            tasksRecyclerAdapter.submitCategories(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}