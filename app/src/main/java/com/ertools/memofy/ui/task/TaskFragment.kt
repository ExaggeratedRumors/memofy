package com.ertools.memofy.ui.task

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ertools.memofy.R
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.databinding.FragmentTaskBinding
import com.ertools.memofy.model.MemofyApplication
import com.ertools.memofy.ui.categories.CategoriesViewModel
import com.ertools.memofy.ui.categories.CategoriesViewModelFactory
import com.ertools.memofy.ui.tasks.TasksViewModel
import com.ertools.memofy.ui.tasks.TasksViewModelFactory
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var tasksViewModel: TasksViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val taskRepository = (requireContext().applicationContext as MemofyApplication).taskRepository
        tasksViewModel = ViewModelProvider(
            this, TasksViewModelFactory(taskRepository)
        )[TasksViewModel::class.java]

        val categoryRepository = (requireContext().applicationContext as MemofyApplication).categoryRepository
        categoriesViewModel = ViewModelProvider(
            this, CategoriesViewModelFactory(categoryRepository)
        )[CategoriesViewModel::class.java]

        configureMenu()
        configureCategory()
        configureTimePicker()
        configureAttachButton()
        return binding.root
    }

    private fun configureMenu() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.action_search).isVisible = false
                menu.findItem(R.id.action_settings).isVisible = false
                menuInflater.inflate(R.menu.form, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.action_save -> {
                        Toast.makeText(requireContext(), "Save", Toast.LENGTH_SHORT).show()
                        saveTask()
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun configureTimePicker() {
        val timePicker = binding.taskTimeInput
        timePicker.setIs24HourView(true)
    }

    private fun configureCategory() {
        val categories = categoriesViewModel.categoriesList.value ?: return
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_menu,
            categories.toMutableList()
        )
        val autoCompleteTextView = binding.taskCategoryInput
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun configureAttachButton() {
        taskViewModel.configureSelectFileLauncher(this)
        binding.taskAttachButton.setOnClickListener {
            taskViewModel.selectFile()
        }

        taskViewModel.selectedFileUri.observe(viewLifecycleOwner) {
            if(it != null) {
                binding.taskAttachButton.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.success_button, null)
                )
                binding.taskAttachButton.setImageResource(R.drawable.ic_menu_gallery)
            }
        }
    }

    private fun saveTask(): Boolean {
        val categories = categoriesViewModel.categoriesList.value
        val title = binding.taskTitleInput.editText?.text.toString()
        val description = binding.taskDescriptionInput.editText?.text.toString()
        val category = categories?.first {
            it.name == binding.taskCategoryInput.text.toString()
        }?.id
        val switch = binding.taskNotificationSwitch.isChecked
        val day = binding.taskDateInput.dayOfMonth
        val month = "%02d".format(binding.taskDateInput.month)
        val year = binding.taskDateInput.year
        val hour = binding.taskTimeInput.hour
        val minute = binding.taskTimeInput.minute
        println("TEST: $day-$month-$year $hour:$minute")
        val timestamp = LocalDate.parse(
            "$day-$month-$year $hour:$minute",
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        ).atStartOfDay(ZoneId.of(ZoneOffset.UTC.id)).toInstant().toEpochMilli()

        if(timestamp < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Invalid date", Toast.LENGTH_SHORT).show()
            return false
        }

        if(title.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid title", Toast.LENGTH_SHORT).show()
            return false
        }

        val task = Task(
            0,
            title,
            "null",
            "null",
            description,
            0,
            switch,
            category,
            taskViewModel.selectedFileUri.value.toString()
        )

        tasksViewModel.insertTask(task)
        findNavController().navigate(R.id.action_nav_task_to_nav_tasks)
        return true
    }

}