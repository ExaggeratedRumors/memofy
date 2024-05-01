package com.ertools.memofy.ui.task_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TaskListViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is task list Fragment"
    }

    val text: LiveData<String> = _text
}