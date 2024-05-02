package com.ertools.memofy.ui.task_list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.R
import com.ertools.memofy.databinding.ItemTaskListBinding
import com.ertools.memofy.model.TaskDTO
import com.google.android.material.snackbar.Snackbar

class TaskListAdapter(
    private val context: Context,
    private val tasks: List<TaskDTO>
) : RecyclerView.Adapter<TaskListAdapter.ItemTaskListHolder>() {

    inner class ItemTaskListHolder(var view: ItemTaskListBinding)
        : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemTaskListHolder {
        val binding = ItemTaskListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemTaskListHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemTaskListHolder, position: Int) {
        val task: TaskDTO = tasks[position]
        val view = holder.view

        view.itemTaskIcon.setImageResource(R.drawable.ic_task)
        view.itemTaskName.text = task.title
        view.itemTaskCategory.text = task.category
        view.itemTaskTime.text = task.finishedAt
        view.itemTaskCardView.setOnClickListener {
            Snackbar.make(it, task.description, Snackbar.LENGTH_SHORT).show()
        }

        if(task.status == 1) {
            view.itemTaskCardView.setBackgroundColor(context.getColor(R.color.purple_200))
        }
    }

    override fun getItemCount(): Int {
        return tasks.size
    }
}