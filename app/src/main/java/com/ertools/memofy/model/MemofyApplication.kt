package com.ertools.memofy.model

import android.app.Application
import com.ertools.memofy.database.MemofyDatabase
import com.ertools.memofy.database.categories.CategoryRepository
import com.ertools.memofy.database.tasks.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MemofyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { MemofyDatabase.getInstance(this, applicationScope) }
    val taskRepository by lazy { TaskRepository(database.taskDAO()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDAO()) }
}