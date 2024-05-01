package com.ertools.memofy.ui.task_list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ertools.memofy.R

class TaskListAdapter(private val items: List<String>) :
    RecyclerView.Adapter<TaskListAdapter.TaskListViewHolder>() {

    class TaskListViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.task_item_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskListViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_list, parent, false)
        return TaskListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskListViewHolder, position: Int) {
        holder.textView.text = items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }
}