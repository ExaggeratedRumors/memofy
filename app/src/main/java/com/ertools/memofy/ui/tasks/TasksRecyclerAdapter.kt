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
import com.ertools.memofy.database.categories.Category
import com.ertools.memofy.database.tasks.Task

class TasksRecyclerAdapter(
    private val context: Context,
    private val tasksViewModel: TasksViewModel
) : RecyclerView.Adapter<TasksRecyclerAdapter.ItemTaskHolder>() {
    private var tasks: List<Task> = emptyList()
    private var categories: List<Category> = emptyList()

    inner class ItemTaskHolder(var view: ItemTaskBinding)
        : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTaskHolder {
        val binding = ItemTaskBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemTaskHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemTaskHolder, position: Int) {
        val task: Task = tasks[position]
        val view = holder.view

        /* Labels */
        view.taskName.text = task.title
        view.taskTime.text = task.finishedAt
        view.taskDescription.text = task.description
        view.taskCardView.setOnClickListener {
            val bundle = bundleOf("task" to task)
            findNavController(view.root).navigate(R.id.action_nav_tasks_to_nav_task, bundle)
        }

        /* Category */
        val category = categories.firstOrNull { it.name == task.category }
        if(category == null) {
            view.taskCategory.text = context.getString(R.string.template)
            view.taskCategoryLayout.backgroundTintList = ColorStateList.valueOf(
                context.getColor(R.color.surface)
            )
        } else {
            view.taskCategory.text = category.name
            view.taskCategoryLayout.backgroundTintList = ColorStateList.valueOf(
                category.color!!
            )
        }

        /* Task status */
        view.taskCheckbox.setOnCheckedChangeListener(null)
        view.taskCheckbox.isChecked = task.completed ?: false
        view.taskCheckbox.setOnCheckedChangeListener { button, _ ->
            tasksViewModel.changeTaskStatus(task, button.isChecked)
        }
        if(task.completed == true) view.taskSurface.setBackgroundColor(context.getColor(R.color.surface_alt))
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