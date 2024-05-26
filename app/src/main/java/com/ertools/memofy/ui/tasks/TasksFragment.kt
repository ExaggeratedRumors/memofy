package com.ertools.memofy.ui.tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.ertools.memofy.R
import com.ertools.memofy.databinding.FragmentTasksBinding
import com.google.android.material.tabs.TabLayoutMediator

class TasksFragment : Fragment() {
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val tasksViewModel: TasksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)

        configureMenu()
        configurePager()
        configureAddTaskButton()
        return binding.root
    }

    private fun configureMenu() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.tasks, menu)
                val filterItem = menu.findItem(R.id.action_filter)
                val spinner = filterItem.actionView as Spinner
                tasksViewModel.categories.observe(viewLifecycleOwner) {
                    val categories = it.map { category -> category.name }.toMutableList()
                    categories.add(0, "Filter")
                    val adapter = ArrayAdapter(
                        requireContext(),
                        R.layout.item_dropdown_menu,
                        categories
                    )
                    spinner.adapter = adapter
                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            if(position == 0) tasksViewModel.changeSelectedCategory(String())
                            else tasksViewModel.changeSelectedCategory(spinner.selectedItem.toString())
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configurePager() {
        val pagerAdapter = TasksPageAdapter(requireActivity())
        binding.tasksViewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tasksTabLayout, binding.tasksViewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Uncompleted"
                1 -> "All tasks"
                else -> "Completed"
            }
        }.attach()
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