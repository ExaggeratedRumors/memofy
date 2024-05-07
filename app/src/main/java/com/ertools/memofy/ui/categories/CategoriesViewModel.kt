package com.ertools.memofy.ui.categories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.database.categories.Category
import com.ertools.memofy.database.categories.CategoryRepository
import kotlinx.coroutines.launch

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
): ViewModel() {
    val categoriesList = categoryRepository.categories.asLiveData()

    fun insertCategory(category: Category) = viewModelScope.launch {
        categoryRepository.insert(category)
    }
}