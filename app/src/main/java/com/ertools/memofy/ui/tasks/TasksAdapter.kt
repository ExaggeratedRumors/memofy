package com.ertools.memofy.ui.tasks

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.R
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.databinding.ItemTaskBinding
import com.google.android.material.snackbar.Snackbar

class TasksAdapter(
    private val context: Context,
    private val tasks: List<Task>
) : RecyclerView.Adapter<TasksAdapter.ItemTaskListHolder>() {

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
        view.taskCategory.text = task.category.toString()
        view.taskTime.text = task.finishedAt
        view.taskCardView.setOnClickListener {
            Snackbar.make(it, task.description?: "", Snackbar.LENGTH_SHORT).show()
        }

        if(task.status == 1) {
            view.taskCardView.setBackgroundColor(context.getColor(R.color.on_secondary_variant))
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}