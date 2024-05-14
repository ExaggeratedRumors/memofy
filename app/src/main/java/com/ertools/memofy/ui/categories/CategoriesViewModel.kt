package com.ertools.memofy.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.database.categories.Category
import com.ertools.memofy.database.categories.CategoryRepository
import com.ertools.memofy.database.tasks.TaskRepository
import com.ertools.memofy.ui.tasks.TasksViewModel
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    val categoriesList = categoryRepository.categories.asLiveData()

    fun insertCategory(category: Category) = viewModelScope.launch {
        categoryRepository.insert(category)
    }
}

class CategoriesViewModelFactory(private val categoryRepository: CategoryRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CategoriesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CategoriesViewModel(categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}