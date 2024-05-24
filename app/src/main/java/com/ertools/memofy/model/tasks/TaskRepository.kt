package com.ertools.memofy.model.tasks

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDAO) {
    var tasks: Flow<List<Task>> = taskDao.select()

    @WorkerThread
    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    @WorkerThread
    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    @WorkerThread
    fun selectAll() = taskDao.select()

    @WorkerThread
    fun selectByCategory(category: String) = taskDao.selectByCategory(category)
}