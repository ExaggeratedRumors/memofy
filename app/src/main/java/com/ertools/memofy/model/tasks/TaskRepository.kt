package com.ertools.memofy.model.tasks

import androidx.annotation.WorkerThread
import com.ertools.memofy.model.categories.Category
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
    suspend fun delete(task: Task) {
        taskDao.delete(task.id!!)
    }

    @WorkerThread
    fun selectByCategory(category: String) = taskDao.selectByCategory(category)

    @WorkerThread
    fun selectByStatus(status: Int) = taskDao.selectByStatus(status)

    @WorkerThread
    fun selectByStatusAndCategory(status: Int, category: String) =
        taskDao.selectByStatusAndCategory(status, category)

}