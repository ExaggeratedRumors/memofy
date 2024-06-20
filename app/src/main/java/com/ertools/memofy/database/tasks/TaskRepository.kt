package com.ertools.memofy.database.tasks

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDAO) {
    var tasks: Flow<List<Task>> = taskDao.select()

    @WorkerThread
    suspend fun insert(task: Task): Long = taskDao.insert(task)

    @WorkerThread
    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    @WorkerThread
    suspend fun delete(task: Task) {
        taskDao.delete(task.id!!)
    }

    @WorkerThread
    fun findByCategory(category: String) = taskDao.selectByCategory(category)

    @WorkerThread
    fun findByStatus(completed: Boolean) = taskDao.selectByStatus(completed)

    @WorkerThread
    fun findByStatusCategory(completed: Boolean, category: String) =
        taskDao.selectByStatusAndCategory(completed, category)

    @WorkerThread
    fun findByQuery(query: String) = taskDao.search(query)

    @WorkerThread
    fun findByQueryCategory(query: String, category: String) = taskDao.searchByCategory(query, category)

    @WorkerThread
    fun findByStatusQuery(query: String, completed: Boolean) = taskDao.searchByStatus(query, completed)

    @WorkerThread
    fun findByStatusQueryCategory(query: String, completed: Boolean, category: String) =
        taskDao.searchByStatusAndCategory(query, completed, category)
}