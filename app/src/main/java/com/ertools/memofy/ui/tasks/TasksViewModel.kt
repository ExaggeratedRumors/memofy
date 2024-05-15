package com.ertools.memofy.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.database.categories.Category
import com.ertools.memofy.database.categories.CategoryRepository
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.database.tasks.TaskRepository
import kotlinx.coroutines.launch

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    val tasks = taskRepository.tasks.asLiveData()
    val categories = categoryRepository.categories.asLiveData()

    fun getCategory(id: Int?): Category? {
        if(id == null) return null
        val p = categoryRepository.get(id).asLiveData().value
        println("TEST: Request $id, got: ${p?.id}")
        return p
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }
}

class TasksViewModelFactory(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(taskRepository, categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}