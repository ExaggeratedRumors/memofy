package com.ertools.memofy.database

import android.app.Application
import com.ertools.memofy.database.annexes.AnnexRepository
import com.ertools.memofy.database.categories.CategoryRepository
import com.ertools.memofy.database.tasks.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MemofyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { MemofyDatabase.getInstance(this, applicationScope) }
    val taskRepository by lazy { TaskRepository(database.taskDAO()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDAO()) }
    val annexRepository by lazy { AnnexRepository(database.annexDAO()) }
}