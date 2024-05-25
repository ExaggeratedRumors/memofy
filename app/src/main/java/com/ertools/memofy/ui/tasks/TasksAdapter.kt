package com.ertools.memofy.ui.tasks

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.R
import com.ertools.memofy.databinding.ItemTaskBinding
import com.ertools.memofy.model.categories.Category
import com.ertools.memofy.model.tasks.Task

class TasksAdapter(
    private val context: Context,
    private val tasksViewModel: TasksViewModel
) : RecyclerView.Adapter<TasksAdapter.ItemTaskListHolder>() {
    private var tasks: List<Task> = emptyList()
    private var categories: List<Category> = emptyList()

    inner class ItemTaskListHolder(var view: ItemTaskBinding)
        : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTaskListHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemTaskListHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemTaskListHolder, position: Int) {
        var task: Task = tasks[position]
        val view = holder.view

        /* Labels */
        view.taskName.text = task.title
        view.taskTime.text = task.finishedAt
        view.taskCardView.setOnClickListener {
            val bundle = bundleOf("task" to task)
            findNavController(view.root).navigate(R.id.action_nav_tasks_to_nav_task, bundle)
        }

        /* Category */
        categories.firstOrNull { it.name == task.category }?.let {
            view.taskCategory.text = it.name
            view.taskCategoryLayout.backgroundTintList = ColorStateList.valueOf(
                it.color!!
            )
        }

        /* Background depends on task status */
        view.taskCheckbox.isChecked = task.status == 1
        view.taskCheckbox.setOnCheckedChangeListener { _, b ->
            val newTask = task.copy(status = if(b) 1 else 0)
            tasksViewModel.updateTask(newTask)
        }
        if(task.status == 1) view.taskSurface.setBackgroundColor(context.getColor(R.color.surface_alt))
        else view.taskSurface.setBackgroundColor(context.getColor(R.color.surface))
    }

    fun submitTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    fun submitCategories(newCategories: List<Category>) {
        categories = newCategories
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}