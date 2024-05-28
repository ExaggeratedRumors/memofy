package com.ertools.memofy.database.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import androidx.appsearch.annotation.Document
import com.ertools.memofy.utils.Utils

@Document
@Entity(tableName=Utils.DATABASE_TASKS_NAMESPACE)
data class Task (
    @Document.Namespace
    val namespace: String = Utils.DATABASE_TASKS_NAMESPACE,
    @Document.StringProperty
    val title: String?,
    @Document.StringProperty
    val createdAt: String?,
    @Document.StringProperty
    val finishedAt: String?,
    @Document.StringProperty
    val description: String?,
    @Document.BooleanProperty
    val completed: Boolean?,
    @Document.BooleanProperty
    val notification: Boolean?,
    @Document.StringProperty
    val category: String?
) : Serializable {
    @Document.Id
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
        val newTask = Task(
            Utils.DATABASE_TASKS_NAMESPACE,
            title,
            createdAt,
            finishedAt,
            description,
            completed,
            notification,
            category
        )
        newTask.id = this.id
        return newTask
    }
}