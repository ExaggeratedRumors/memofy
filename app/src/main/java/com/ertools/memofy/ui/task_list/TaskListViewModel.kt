package com.ertools.memofy.ui.task_list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ertools.memofy.model.TaskDTO
import com.ertools.memofy.model.TaskRepository

class TaskListViewModel : ViewModel() {
    private var tasksRepository = TaskRepository().apply {
        this.fillData()
    }
    var tasksList = MutableLiveData<List<TaskDTO>>()

    init {
        tasksList.value = tasksRepository.tasks
    }
}