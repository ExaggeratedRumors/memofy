package com.ertools.memofy.model.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="tasks")
data class Task (
    val title: String?,
    val createdAt: String?,
    val finishedAt: String?,
    val description: String?,
    val completed: Boolean?,
    val notification: Boolean?,
    val category: String?
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    fun clone(
        title: String? = this.title,
        createdAt: String? = this.createdAt,
        finishedAt: String? = this.finishedAt,
        description: String? = this.description,
        completed: Boolean? = this.completed,
        notification: Boolean? = this.notification,
        category: String? = this.category
    ): Task {
        val newTask = Task(title, createdAt, finishedAt, description, completed, notification, category)
        newTask.id = this.id
        return newTask
    }
}