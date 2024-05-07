package com.ertools.memofy.ui.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.ertools.memofy.R
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.databinding.FragmentTaskBinding
import com.ertools.memofy.ui.categories.CategoriesViewModel
import com.ertools.memofy.ui.tasks.TasksViewModel

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!
    private val tasksViewModel by lazy {
        ViewModelProvider(this)[TasksViewModel::class.java]
    }
    private val categoriesViewModel by lazy {
        ViewModelProvider(this)[CategoriesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        initCategoriesAdapter()

        return binding.root
    }


    private fun initTimePicker() {
        val timePicker = binding.taskTimeInput
        timePicker.setIs24HourView(true)
    }

    private fun initCategoriesAdapter() {
        val categories = categoriesViewModel.categoriesList.value ?: return
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_menu,
            categories.toMutableList()
        )
        val autoCompleteTextView = binding.taskCategoryInput
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun initSaveButton() {
        val categories = categoriesViewModel.categoriesList.value
        binding.taskSaveButton.setOnClickListener {
            val title = binding.taskTitleInput.editText?.text.toString()
            val description = binding.taskDescriptionInput.editText?.text.toString()
            val category = categories?.first {
                it.name == binding.taskCategoryInput.text.toString()
            }?.id
            val switch = binding.taskNotificationSwitch.isChecked

            if(title.isEmpty()) {
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
                "null"
            )
            tasksViewModel.insertTask(task)
        }
    }

}