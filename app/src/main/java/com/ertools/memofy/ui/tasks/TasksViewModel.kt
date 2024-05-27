package com.ertools.memofy.ui.tasks

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.database.categories.CategoryRepository
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.database.tasks.TaskRepository
import kotlinx.coroutines.launch

class TasksViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    private val selectedCategory = MutableLiveData<String>()
    val categories = categoryRepository.categories.asLiveData()

    private val allTasks = taskRepository.tasks.asLiveData()

    private val completedTasks = selectedCategory.switchMap {
        if(it.isEmpty()) taskRepository.selectByStatus(true).asLiveData()
        else taskRepository.selectByStatusAndCategory(true, it).asLiveData()
    }
    private val uncompletedTasks = selectedCategory.switchMap {
        if(it.isEmpty()) taskRepository.selectByStatus(false).asLiveData()
        else taskRepository.selectByStatusAndCategory(false, it).asLiveData()
    }

    fun insertTask(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }

    fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }

    fun removeTask(task: Task) = viewModelScope.launch {
        taskRepository.delete(task)
    }

    fun changeSelectedCategory(category: String) {
        selectedCategory.value = category
    }

    fun getDataByStatus(status: Int?) = when(status) {
        0 -> uncompletedTasks
        1 -> completedTasks
        else -> allTasks
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