package com.ertools.memofy.ui.task

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.database.annexes.Annex
import com.ertools.memofy.database.annexes.AnnexRepository
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.database.tasks.TaskRepository
import com.ertools.memofy.files.AnnexFileManager
import com.ertools.memofy.notification.TaskNotificationManager
import com.ertools.memofy.utils.Utils
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val annexRepository: AnnexRepository
): ViewModel() {
    private val _selectedTask = MutableLiveData<Task?>()
    val selectedTask: LiveData<Task?> = _selectedTask
    private val _annexes: MutableLiveData<List<Annex>> = MutableLiveData<List<Annex>>().apply {
        value = ArrayList()
    }
    val annexes: LiveData<List<Annex>> = _annexes
    private val fileManager: AnnexFileManager = AnnexFileManager()
    private val notificationManager: TaskNotificationManager = TaskNotificationManager()


    /** Manage annexes in application **/

    fun setTask(task: Task?) {
        _selectedTask.value = task
        if(task == null) return
        viewModelScope.launch {
            annexRepository.getByTaskId(task.id!!).collect { list ->
                val newAnnexes = ArrayList<Annex>()
                list.forEach { annex ->
                    newAnnexes.add(annex)
                }
                _annexes.setValue(newAnnexes)
            }
        }
    }

    fun addTask(activity: Activity, task: Task) = viewModelScope.launch {
        insertTask(task) { taskId ->
            _annexes.value?.forEach {
                it.taskId = taskId.toInt()
            }
            insertAnnexes(annexes.value!!)
            saveAnnexFiles(activity, annexes.value!!)
            scheduleTaskNotification(activity, task)
        }
    }

    fun removeTask() {
        val task = selectedTask.value ?: return
        cancelTaskNotification(selectedTask.value!!)
        removeAnnexFiles(annexes.value!!)
        deleteAnnexesFromTask(selectedTask.value!!)
        deleteTask(task)
    }

    fun updateTaskData(activity: Activity) {
        val task = selectedTask.value ?: return
        insertAnnexes(annexes.value!!)
        removeAnnexFiles(annexes.value!!)
        saveAnnexFiles(activity, annexes.value!!)
        updateTask(task)
    }

    fun addAnnex(newAnnex: Annex) {
        val newAnnexes = _annexes.value?.toMutableList()
        newAnnexes?.apply {
            this.add(newAnnex)
            _annexes.value = this
        }
    }

    fun removeAnnex(annex: Annex) {
        _annexes.value = _annexes.value?.filter { it.id != annex.id && it.name == annex.name}
        if(_selectedTask.value == null) return
        removeAnnexFile(annex)
        deleteAnnex(annex)
    }

    fun cancelAnnexes() {
        _annexes.value = ArrayList()
    }



    /** Database **/
    private fun insertTask(task: Task, callback: (Long) -> Unit) = viewModelScope.launch {
        val taskId = taskRepository.insert(task)
        callback(taskId)
    }

    private fun updateTask(task: Task) = viewModelScope.launch {
        taskRepository.update(task)
    }

    private fun deleteTask(task: Task) = viewModelScope.launch {
        taskRepository.delete(task)
    }

    private fun insertAnnexes(annexes: List<Annex>) {
        viewModelScope.launch {
            annexes.forEach { annex ->
                annexRepository.insert(annex)
            }
        }
    }

    private fun deleteAnnex(annex: Annex) = viewModelScope.launch {
        annexRepository.delete(annex)
    }

    private fun deleteAnnexesFromTask(task: Task) = viewModelScope.launch {
        annexRepository.deleteByTaskId(task.id!!)
    }


    /** Files **/
    fun configureFileManager(fragment: Fragment) {
        fileManager.configureSelectFileLauncher(fragment, this)
    }

    private fun saveAnnexFiles(activity: Activity, annexes: List<Annex>) {
        annexes.forEach { annex ->
            fileManager.saveFileToExternalStorage(activity, annex)
        }
    }

    private fun removeAnnexFile(annex: Annex) = fileManager.deleteFromExternalStorage(annex)

    private fun removeAnnexFiles(annexes: List<Annex>) {
        annexes.forEach { annex ->
            fileManager.deleteFromExternalStorage(annex)
        }
    }

    fun selectFile() {
        fileManager.selectFile()
    }


    /** Notifications **/
    private fun scheduleTaskNotification(context: Context, task: Task) {
        notificationManager.scheduleTaskNotification(context, task, Utils.NOTIFICATION_TIME_DEFAULT)
    }

    private fun cancelTaskNotification(task: Task) {

    }
}

class TaskViewModelFactory(
    private val taskRepository: TaskRepository,
    private val annexRepository: AnnexRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskRepository, annexRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}