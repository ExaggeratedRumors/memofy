package com.ertools.memofy.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.R
import com.ertools.memofy.model.categories.Category
import com.ertools.memofy.model.tasks.Task
import com.ertools.memofy.databinding.ItemTaskBinding
import com.google.android.material.snackbar.Snackbar

class TasksAdapter(
    private val context: Context
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
        val task: Task = tasks[position]
        val view = holder.view

        view.taskIcon.setImageResource(R.drawable.ic_task)
        view.taskName.text = task.title


        categories.firstOrNull { it.name == task.category }?.let {
            view.taskCategory.text = it.name
            view.taskCategory.setTextColor(it.resourceColor!!)
        }

        view.taskTime.text = task.finishedAt
        view.taskCardView.setOnClickListener {
            Snackbar.make(it, task.description?: "", Snackbar.LENGTH_SHORT).show()
        }

        if(task.status == 1) {
            view.taskCardView.setBackgroundColor(context.getColor(R.color.primary_variant))
        }
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