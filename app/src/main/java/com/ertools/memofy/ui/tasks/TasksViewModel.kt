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
    private val enteredQuery = MutableLiveData<String>().apply {
        value = String()
    }
    private val selectedCategory = MutableLiveData<String>().apply {
        value = String()
    }
    val categories = categoryRepository.categories.asLiveData()

    private val _allTasks = MediatorLiveData<Pair<String, String>>().apply {
        addSource(enteredQuery) {
            value = it to selectedCategory.value!!
        }
        addSource(selectedCategory) {
            value = enteredQuery.value!! to it
        }
    }
    private val allTasks: LiveData<List<Task>> = _allTasks.switchMap { (query, category) ->
        fetchTasks(query, category, null)
    }

    private val _completedTasks = MediatorLiveData<Pair<String, String>>().apply {
        addSource(enteredQuery) {
            value = it to selectedCategory.value!!
        }
        addSource(selectedCategory) {
            value = enteredQuery.value!! to it
        }
    }
    private val completedTasks: LiveData<List<Task>> = _completedTasks.switchMap { (query, category) ->
        fetchTasks(query, category, true)
    }

    private val _uncompletedTasks = MediatorLiveData<Pair<String, String>>().apply {
        addSource(enteredQuery) {
            value = it to selectedCategory.value!!
        }
        addSource(selectedCategory) {
            value = enteredQuery.value!! to it
        }
    }
    private val uncompletedTasks: LiveData<List<Task>> = _uncompletedTasks.switchMap { (query, category) ->
        fetchTasks(query, category, false)
    }


    /** Live data **/

    private fun fetchTasks(
        query: String,
        category: String,
        completed: Boolean?
    ): LiveData<List<Task>> {
        if(query.isEmpty() && category.isEmpty() && completed == null)
            return taskRepository.tasks.asLiveData()
        if(query.isEmpty() && completed == null)
            return taskRepository.findByCategory(category).asLiveData()
        if(category.isEmpty() && completed == null)
            return taskRepository.findByQuery(query).asLiveData()
        if(query.isEmpty() && category.isEmpty())
            return taskRepository.findByStatus(completed!!).asLiveData()
        if(category.isEmpty())
            return taskRepository.findByStatusQuery(query, completed!!).asLiveData()
        if(query.isEmpty())
            return taskRepository.findByStatusCategory(completed!!, category).asLiveData()
        if(completed == null)
            return taskRepository.findByQueryCategory(query, category).asLiveData()
        return taskRepository.findByStatusQueryCategory(query, completed, category).asLiveData()
    }

    fun getDataByStatus(status: Int?) = when(status) {
        0 -> uncompletedTasks
        1 -> completedTasks
        else -> allTasks
    }

    fun changeSelectedCategory(category: String) {
        selectedCategory.value = category
    }

    fun changeEnteredQuery(query: String) {
        enteredQuery.value = query
    }

    /** Database **/
    fun changeTaskStatus(task: Task, completed: Boolean) = viewModelScope.launch {
        taskRepository.update(task.clone(completed = completed))
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