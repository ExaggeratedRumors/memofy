package com.ertools.memofy.database.categories

import androidx.annotation.WorkerThread

class CategoryRepository(private val categoryDao: CategoryDAO) {
    val categories = categoryDao.select()

    @WorkerThread
    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }
}