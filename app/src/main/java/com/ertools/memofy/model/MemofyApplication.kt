package com.ertools.memofy.model

import android.app.Application
import com.ertools.memofy.model.annexes.AnnexRepository
import com.ertools.memofy.model.categories.CategoryRepository
import com.ertools.memofy.model.tasks.TaskRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MemofyApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { MemofyDatabase.getInstance(this, applicationScope) }
    val taskRepository by lazy { TaskRepository(database.taskDAO()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDAO()) }
    val annexesRepository by lazy { AnnexRepository(database.annexDAO()) }
}