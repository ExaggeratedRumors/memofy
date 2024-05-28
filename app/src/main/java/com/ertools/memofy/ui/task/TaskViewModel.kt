package com.ertools.memofy.ui.task

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.database.annexes.Annex
import com.ertools.memofy.database.annexes.AnnexRepository
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.files.AnnexesFileManager
import kotlinx.coroutines.launch

class TaskViewModel(
    private val annexRepository: AnnexRepository
): ViewModel() {
    private val _selectedTask = MutableLiveData<Task?>()
    val selectedTask: LiveData<Task?> = _selectedTask
    private val _annexes: MutableLiveData<List<Annex>> = MutableLiveData<List<Annex>>().apply {
        value = ArrayList()
    }
    val annexes: LiveData<List<Annex>> = _annexes
    private val fileManager: AnnexesFileManager = AnnexesFileManager()

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

    fun saveAnnexes(fragment: Fragment) {
        _annexes.value?.forEach { annex ->
            fileManager.saveFileToExternalStorage(annex, fragment)
        }

        viewModelScope.launch {
            _annexes.value?.forEach { annex ->
                annexRepository.insert(annex)
            }
        }
    }

    fun addAnnex(newAnnex: Annex) {
        val newAnnexes = _annexes.value?.toMutableList()
        newAnnexes?.apply {
            this.add(newAnnex)
            _annexes.value = this
        }
    }

    fun deleteAnnex(annex: Annex) {
        _annexes.value = _annexes.value?.filter { it.id != annex.id }
        if(_selectedTask.value != null) {
            fileManager.deleteFromExternalStorage(annex)
            viewModelScope.launch {
                annexRepository.delete(annex)
            }
        }
    }

    fun cancelAnnexes() {
        _annexes.value = ArrayList()
    }

    fun deleteAnnexesFromTask() = viewModelScope.launch {
        _selectedTask.value?.id?.let {
            _annexes.value?.forEach { annex ->
                fileManager.deleteFromExternalStorage(annex)
            }
            annexRepository.deleteByTaskId(it)
        }
    }

    fun configureFileManager(fragment: Fragment) {
        fileManager.configureSelectFileLauncher(fragment, this)
    }

    fun selectFile() {
        fileManager.selectFile()
    }
}

class TaskViewModelFactory(
    private val annexRepository: AnnexRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(annexRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}