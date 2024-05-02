package com.ertools.memofy.model

data class TaskDTO (
    val id: Int,
    val title: String,
    val createdAt: String,
    val finishedAt: String?,
    val description: String,
    val status: Int,
    val notification: Boolean,
    val category: String,
    val attachment: String
)