package com.ertools.memofy.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val taskViewModel: TaskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        val taskRepository = (requireContext().applicationContext as MemofyApplication).taskRepository
        val tasksViewModel by lazy {
            ViewModelProvider(
                this, TasksViewModelFactory(taskRepository)
            )[TasksViewModel::class.java]
        }

        val categoryRepository = (requireContext().applicationContext as MemofyApplication).categoryRepository
        val categoriesViewModel by lazy {
            ViewModelProvider(
                this, CategoriesViewModelFactory(categoryRepository)
            )[CategoriesViewModel::class.java]
        }

        configureCategory(categoriesViewModel)
        configureTimePicker()
        configureAttachButton(taskViewModel)
        configureSaveButton(taskViewModel, tasksViewModel, categoriesViewModel)

        return binding.root
    }

    private fun configureTimePicker() {
        val timePicker = binding.taskTimeInput
        timePicker.setIs24HourView(true)
    }

    private fun configureCategory(categoriesViewModel: CategoriesViewModel) {
        val categories = categoriesViewModel.categoriesList.value ?: return
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_menu,
            categories.toMutableList()
        )
        val autoCompleteTextView = binding.taskCategoryInput
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun configureAttachButton(taskViewModel: TaskViewModel) {
        taskViewModel.configureSelectFileLauncher(this)
        binding.taskAttachButton.setOnClickListener {
            taskViewModel.selectFile()
        }
    }

    private fun configureSaveButton(
        taskViewModel: TaskViewModel,
        tasksViewModel: TasksViewModel,
        categoriesViewModel: CategoriesViewModel
    ) {
        val categories = categoriesViewModel.categoriesList.value
        binding.taskSaveButton.setOnClickListener {
            val title = binding.taskTitleInput.editText?.text.toString()
            val description = binding.taskDescriptionInput.editText?.text.toString()
            val category = categories?.first {
                it.name == binding.taskCategoryInput.text.toString()
            }?.id
            val switch = binding.taskNotificationSwitch.isChecked
            val day = binding.taskDateInput.dayOfMonth
            val month = binding.taskDateInput.month
            val year = binding.taskDateInput.year
            val hour = binding.taskTimeInput.hour
            val minute = binding.taskTimeInput.minute
            val timestamp = LocalDate.parse(
                "$day-$month-$year $hour:$minute",
                DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
            ).atStartOfDay(ZoneId.of(ZoneOffset.UTC.id)).toInstant().toEpochMilli()

            if(timestamp < System.currentTimeMillis()) {
                Toast.makeText(requireContext(), "Invalid date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(title.isEmpty()) {
                Toast.makeText(requireContext(), "Invalid title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
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
        }
    }

}