package com.ertools.memofy.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.database.tasks.TaskRepository
import kotlinx.coroutines.launch

class TasksViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    val tasksList = taskRepository.tasks.asLiveData()

    fun insertTask(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }
}

class TasksViewModelFactory(private val taskRepository: TaskRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TasksViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}