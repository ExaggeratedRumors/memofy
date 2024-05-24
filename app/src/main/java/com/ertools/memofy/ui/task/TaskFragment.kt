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
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.R
import com.ertools.memofy.model.tasks.Task
import com.ertools.memofy.databinding.FragmentTaskBinding
import com.ertools.memofy.model.MemofyApplication
import com.ertools.memofy.ui.categories.CategoriesViewModel
import com.ertools.memofy.ui.categories.CategoriesViewModelFactory
import com.ertools.memofy.ui.tasks.TasksViewModel
import com.ertools.memofy.ui.tasks.TasksViewModelFactory
import com.ertools.memofy.utils.serializable
import com.ertools.memofy.utils.timestampToTime
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
    private var task: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        val taskRepository = (requireContext().applicationContext as MemofyApplication).taskRepository
        val categoryRepository = (requireContext().applicationContext as MemofyApplication).categoryRepository
        val annexRepository = (requireContext().applicationContext as MemofyApplication).annexesRepository

        taskViewModel = ViewModelProvider(
                this, TaskViewModelFactory(annexRepository)
        )[TaskViewModel::class.java]

        tasksViewModel = ViewModelProvider(
            this, TasksViewModelFactory(taskRepository, categoryRepository)
        )[TasksViewModel::class.java]

        categoriesViewModel = ViewModelProvider(
            this, CategoriesViewModelFactory(categoryRepository)
        )[CategoriesViewModel::class.java]

        fillDataByTask()
        configureMenu()
        configureCategory()
        configureTimePicker()
        configureAnnexes()
        configureAnnexes()
        return binding.root
    }

    private fun fillDataByTask() {
        task = arguments?.serializable<Task>("task")
        task?.let { taskViewModel.setTask(it) } ?: return

        binding.taskTitleInput.editText?.setText(task?.title)
        binding.taskDescriptionInput.editText?.setText(task?.description)
        binding.taskCategoryInput.setText(task?.category)
        binding.taskNotificationSwitch.isChecked = task?.notification ?: false

        /** Fill time/data picker **/
        if(task?.finishedAt == null || task?.finishedAt!!.length < 10) return
        binding.taskDateInput.updateDate(
            task?.finishedAt?.substring(0, 4)?.toInt() ?: 0,
            (task?.finishedAt?.substring(6, 7)?.toInt() ?: 1) - 1,
            task?.finishedAt?.substring(9, 10)?.toInt() ?: 0
        )
        if(task?.finishedAt == null || task?.finishedAt!!.length < 16) return
        binding.taskTimeInput.hour = task?.finishedAt?.substring(11, 13)?.toInt() ?: 0
        binding.taskTimeInput.minute = task?.finishedAt?.substring(14, 16)?.toInt() ?: 0
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
        tasksViewModel.categories.observe(viewLifecycleOwner) {
            val names = it.map { category -> category.name }.toMutableList()
            if(names.size > 0) binding.taskCategoryInput.setText(names[0], false)
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_dropdown_menu,
                names
            )
            val autoCompleteTextView = binding.taskCategoryInput
            autoCompleteTextView.setAdapter(adapter)
        }
    }

    private fun configureAnnexes() {
        /** Recycler view **/
        binding.taskFilesRecycler.layoutManager = LinearLayoutManager(requireContext())
        taskViewModel.annexes.observe(viewLifecycleOwner) {
            val annexAdapter = AnnexAdapter(requireContext(), taskViewModel, it)
            binding.taskFilesRecycler.adapter = annexAdapter
            binding.taskFilesRecycler.isNestedScrollingEnabled = false
            if(it.isNotEmpty()) {
                binding.taskAttachButton.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.success, null)
                )
                binding.taskAttachButton.setImageResource(R.drawable.ic_attachment_horizontal)
            } else {
                binding.taskAttachButton.backgroundTintList = ColorStateList.valueOf(
                    resources.getColor(R.color.primary, null)
                )
                binding.taskAttachButton.setImageResource(R.drawable.ic_attachment)
            }
        }

        /** Attach button **/
        taskViewModel.configureSelectFileLauncher(this)
        binding.taskAttachButton.setOnClickListener {
            taskViewModel.selectFile()
        }
    }

    private fun saveTask(): Boolean {
        val title = binding.taskTitleInput.editText?.text.toString()
        val description = binding.taskDescriptionInput.editText?.text.toString()
        val category = binding.taskCategoryInput.text.toString().let {
            it.ifEmpty { null }
        }
        val switch = binding.taskNotificationSwitch.isChecked
        val day = "%02d".format(binding.taskDateInput.dayOfMonth)
        val month = "%02d".format(binding.taskDateInput.month + 1)
        val year = "%04d".format(binding.taskDateInput.year)
        val hour = "%02d".format(binding.taskTimeInput.hour)
        val minute = "%02d".format(binding.taskTimeInput.minute)
        val date = "$year-$month-$day $hour:$minute"

        val timestamp = LocalDate.parse(
            date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        ).atStartOfDay(ZoneId.of(ZoneOffset.UTC.id)).toInstant().toEpochMilli()

        if(timestamp < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Invalid date", Toast.LENGTH_SHORT).show()
            return false
        }

        if(title.isEmpty()) {
            Toast.makeText(requireContext(), "Invalid title", Toast.LENGTH_SHORT).show()
            return false
        }

        val isUpdate = task != null
        task = Task(
            title,
            timestampToTime(System.currentTimeMillis()),
            date,
            description,
            0,
            switch,
            category
        )

        taskViewModel.saveAnnexes()

        if(isUpdate) tasksViewModel.updateTask(task!!)
        else tasksViewModel.insertTask(task!!)
        findNavController().navigate(R.id.action_nav_task_to_nav_tasks)
        return true
    }
}