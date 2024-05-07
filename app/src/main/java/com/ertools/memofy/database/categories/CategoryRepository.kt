package com.ertools.memofy.database.categories

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDAO) {
    val categories: Flow<List<Category>> = categoryDao.select()

    @WorkerThread
    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }
}