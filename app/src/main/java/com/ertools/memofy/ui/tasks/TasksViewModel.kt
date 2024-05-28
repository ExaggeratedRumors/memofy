package com.ertools.memofy.ui.tasks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
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
    private val enteredQuery = MutableLiveData<String>()
    private val selectedCategory = MutableLiveData<String>()
    val categories = categoryRepository.categories.asLiveData()
    private val _allTasks = MediatorLiveData<List<Task>>()
    private val allTasks: LiveData<List<Task>> get() = _allTasks
    private val _completedTasks = MediatorLiveData<List<Task>>()
    private val completedTasks: LiveData<List<Task>> get() = _completedTasks
    private val _uncompletedTasks = MediatorLiveData<List<Task>>()
    private val uncompletedTasks: LiveData<List<Task>> get() = _uncompletedTasks


    /*private val allTasks = selectedCategory.switchMap {
        if(it.isEmpty()) taskRepository.tasks.asLiveData()
        else taskRepository.selectByCategory(it).asLiveData()
    }

    private val completedTasks = selectedCategory.switchMap {
        if(it.isEmpty()) taskRepository.selectByStatus(true).asLiveData()
        else taskRepository.selectByStatusAndCategory(true, it).asLiveData()
    }

    private val uncompletedTasks = selectedCategory.switchMap {
        if(it.isEmpty()) taskRepository.selectByStatus(false).asLiveData()
        else taskRepository.selectByStatusAndCategory(false, it).asLiveData()
    }*/

    init {
        _allTasks.addSource(enteredQuery) { query ->
            _allTasks.value = fetchTasks(query, selectedCategory.value ?: String(), null).value
        }
        _allTasks.addSource(selectedCategory) { category ->
            _allTasks.value = fetchTasks(enteredQuery.value ?: String(), category, null).value
        }
        _completedTasks.addSource(enteredQuery) { query ->
            _completedTasks.value = fetchTasks(query, selectedCategory.value ?: String(), true).value
        }
        _completedTasks.addSource(selectedCategory) { category ->
            _completedTasks.value = fetchTasks(enteredQuery.value ?: String(), category, true).value
        }
        _uncompletedTasks.addSource(enteredQuery) { query ->
            _uncompletedTasks.value = fetchTasks(query, selectedCategory.value ?: String(), false).value
        }
        _uncompletedTasks.addSource(selectedCategory) { category ->
            _uncompletedTasks.value = fetchTasks(enteredQuery.value ?: String(), category, false).value
        }
    }

    private fun fetchTasks(
        query: String,
        category: String,
        completed: Boolean?
    ): LiveData<List<Task>> {
        if(query.isEmpty() && category.isEmpty() && completed == null)
            return taskRepository.tasks.asLiveData()
        if(query.isEmpty() && completed == null)
            return taskRepository.selectByCategory(category).asLiveData()
        if(category.isEmpty() && completed == null)
            return taskRepository.search(query).asLiveData()
        if(query.isEmpty() && category.isEmpty())
            return taskRepository.selectByStatus(completed!!).asLiveData()
        if(category.isEmpty())
            return taskRepository.searchByStatus(query, completed!!).asLiveData()
        if(query.isEmpty())
            return taskRepository.selectByStatusAndCategory(completed!!, category).asLiveData()
        if(completed == null)
            return taskRepository.searchByCategory(query, category).asLiveData()
        return taskRepository.searchByStatusAndCategory(query, completed, category).asLiveData()
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

    fun changeEnteredQuery(query: String) {
        enteredQuery.value = query
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