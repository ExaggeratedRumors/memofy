package com.ertools.memofy.model.categories

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDAO) {
    val categories: Flow<List<Category>> = categoryDao.select()

    @WorkerThread
    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }

    @WorkerThread
    suspend fun delete(category: Category) {
        categoryDao.delete(category.id!!)
    }

    @WorkerThread
    fun get(id: Int): Flow<Category> {
        return categoryDao.selectById(id)
    }

    @WorkerThread
    fun getByName(name: String): Flow<Category> {
        return categoryDao.selectByName(name)
    }
}