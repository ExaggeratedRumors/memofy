package com.ertools.memofy.database.tasks

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="tasks")
data class Task (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val title: String?,
    val createdAt: String?,
    val finishedAt: String?,
    val description: String?,
    val status: Int?,
    val notification: Boolean?,
    val category: Int?,
    val attachment: String?
)