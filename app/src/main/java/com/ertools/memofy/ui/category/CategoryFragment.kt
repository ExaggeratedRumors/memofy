package com.ertools.memofy.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ertools.memofy.R
import com.ertools.memofy.databinding.FragmentCategoryBinding
import com.ertools.memofy.model.tasks.Task
import com.ertools.memofy.databinding.FragmentTaskBinding
import com.ertools.memofy.model.MemofyApplication
import com.ertools.memofy.ui.categories.CategoriesViewModel
import com.ertools.memofy.ui.categories.CategoriesViewModelFactory
import com.ertools.memofy.utils.timestampToTime
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class CategoryFragment : Fragment() {
    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoriesViewModel: CategoriesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)
        categoryViewModel = ViewModelProvider(this)[CategoryViewModel::class.java]

        val categoryRepository = (requireContext().applicationContext as MemofyApplication).categoryRepository
        categoriesViewModel = ViewModelProvider(
            this, CategoriesViewModelFactory(categoryRepository)
        )[CategoriesViewModel::class.java]

        configureMenu()
        return binding.root
    }

    private fun configureMenu() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.action_settings).isVisible = false
                menuInflater.inflate(R.menu.form, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.action_save -> {
                        Toast.makeText(requireContext(), "Save", Toast.LENGTH_SHORT).show()
                        saveCategory()
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun saveCategory(): Boolean {
        val name = binding.taskTitleInput.editText?.text.toString()
        val description = binding.taskDescriptionInput.editText?.text.toString()
        val category = binding.taskCategoryInput.text.toString()
        val switch = binding.taskNotificationSwitch.isChecked
        val day = "%02d".format(binding.taskDateInput.dayOfMonth)
        val month = "%02d".format(binding.taskDateInput.month)
        val year = "%04d".format(binding.taskDateInput.year)
        val hour = "%02d".format(binding.taskTimeInput.hour)
        val minute = "%02d".format(binding.taskTimeInput.minute)
        val date = "$day-$month-$year $hour:$minute"

        val timestamp = LocalDate.parse(
            date, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
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
            title,
            timestampToTime(System.currentTimeMillis()),
            date,
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