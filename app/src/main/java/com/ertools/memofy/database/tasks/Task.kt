package com.ertools.memofy.database.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="tasks")
data class Task (
    val title: String?,
    val createdAt: String?,
    val finishedAt: String?,
    val description: String?,
    val status: Int?,
    val notification: Boolean?,
    val category: String?,
    val attachment: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}