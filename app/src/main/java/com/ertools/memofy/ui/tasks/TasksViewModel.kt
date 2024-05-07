package com.ertools.memofy.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.database.tasks.TaskRepository
import kotlinx.coroutines.launch

class TaskListViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {
    val tasksList = taskRepository.tasks.asLiveData()

    fun insertTask(task: Task) = viewModelScope.launch {
        taskRepository.insert(task)
    }
}

class TaskListViewModelFactory(private val taskRepository: TaskRepository)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}