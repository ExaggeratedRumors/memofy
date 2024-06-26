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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ertools.memofy.R
import com.ertools.memofy.database.MemofyApplication
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.databinding.FragmentTaskBinding
import com.ertools.memofy.ui.categories.CategoriesViewModel
import com.ertools.memofy.ui.categories.CategoriesViewModelFactory
import com.ertools.memofy.utils.Utils
import com.ertools.memofy.utils.serializable
import com.ertools.memofy.utils.timestampToTime
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class TaskFragment : Fragment() {
    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val taskViewModel: TaskViewModel by activityViewModels {
        TaskViewModelFactory(
            (requireActivity().applicationContext as MemofyApplication).taskRepository,
            (requireActivity().applicationContext as MemofyApplication).annexRepository
        )
    }
    private val categoriesViewModel: CategoriesViewModel by activityViewModels {
        CategoriesViewModelFactory(
            (requireActivity().applicationContext as MemofyApplication).categoryRepository
        )
    }
    private var task: Task? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        fillDataByTask()
        configureMenu()
        configureCategory()
        configureTimePicker()
        configureAnnexes()
        return binding.root
    }

    private fun fillDataByTask() {
        task = arguments?.serializable<Task>(Utils.BUNDLE_TASK)
        taskViewModel.setTask(task)
        if(task == null) return
        task?.let { taskViewModel.setTask(it) } ?: return

        binding.taskTitleInput.editText?.setText(task?.title)
        binding.taskDescriptionInput.editText?.setText(task?.description)
        binding.taskCategoryInput.setText(task?.category)
        binding.taskNotificationSwitch.isChecked = task?.notification ?: false

        /** Fill time/data picker **/
        if(task?.finishedAt == null || task?.finishedAt!!.length < 10) return
        binding.taskDateInput.updateDate(
            (task?.finishedAt?.substring(0, 4)?.toInt() ?: 0),
            (task?.finishedAt?.substring(5, 7)?.toInt() ?: 1) - 1,
            task?.finishedAt?.substring(8, 10)?.toInt() ?: 0
        )
        if(task?.finishedAt == null || task?.finishedAt!!.length < 16) return
        binding.taskTimeInput.hour = task?.finishedAt?.substring(11, 13)?.toInt() ?: 0
        binding.taskTimeInput.minute = task?.finishedAt?.substring(14, 16)?.toInt() ?: 0
    }

    private fun configureMenu() {
        requireActivity().addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.findItem(R.id.action_settings).isVisible = false
                if(task == null) menuInflater.inflate(R.menu.new_form, menu)
                else menuInflater.inflate(R.menu.edit_form, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId) {
                    R.id.action_save -> saveTask()
                    R.id.action_delete -> removeTask()
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
        categoriesViewModel.categories.observe(viewLifecycleOwner) {
            val names = it.map { category -> category.name }.toMutableList()
            if(names.size > 0) binding.taskCategoryInput.setText(names[0], false)
            val adapter = ArrayAdapter(
                requireContext(),
                R.layout.item_dropdown_surface,
                names
            )
            val autoCompleteTextView = binding.taskCategoryInput
            autoCompleteTextView.setAdapter(adapter)
        }
    }

    private fun configureAnnexes() {
        taskViewModel.cancelAnnexes()

        /** Recycler view **/
        binding.taskFilesRecycler.layoutManager = LinearLayoutManager(requireContext())
        taskViewModel.annexes.observe(viewLifecycleOwner) {
            val annexAdapter = AnnexesRecyclerAdapter(requireContext(), taskViewModel, it)
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
        taskViewModel.configureFileManager(this)
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

        val timestamp = LocalDate.parse(date, DateTimeFormatter.ofPattern(Utils.DATE_FORMAT))
            .atTime(binding.taskTimeInput.hour, binding.taskTimeInput.minute)
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()

        if(timestamp < System.currentTimeMillis()) {
            Toast.makeText(requireContext(), "Date cannot be earlier than now", Toast.LENGTH_SHORT).show()
            return false
        }

        if(title.isEmpty()) {
            Toast.makeText(requireContext(), "You must enter title", Toast.LENGTH_SHORT).show()
            return false
        }

        val newTask = Task(
            title,
            timestampToTime(System.currentTimeMillis()),
            date,
            description,
            false,
            switch,
            category
        )

        task?.id?.let {
            newTask.id = it
            taskViewModel.updateTaskData(requireActivity(), newTask)
        } ?: taskViewModel.addTask(requireActivity(), newTask)

        findNavController().navigate(R.id.action_nav_task_to_nav_tasks)
        return true
    }

    private fun removeTask(): Boolean {
        taskViewModel.removeTask(requireContext())

        findNavController().navigate(R.id.action_nav_task_to_nav_tasks)
        return true
    }
}