package com.ertools.memofy.ui.task

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ertools.memofy.model.annexes.Annex
import com.ertools.memofy.model.annexes.AnnexRepository
import com.ertools.memofy.model.tasks.Task
import com.ertools.memofy.utils.Utils
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class TaskViewModel(
    private val annexRepository: AnnexRepository,
): ViewModel() {
    private val selectedTask = MutableLiveData<Task?>()
    private val _selectedAnnexes = MutableLiveData<ArrayList<Annex>>()
    val selectedAnnexes: LiveData<ArrayList<Annex>> = _selectedAnnexes
    private var filerLauncher: ActivityResultLauncher<Intent>? = null

    fun setTask(task: Task) = viewModelScope.launch {
        selectedTask.value = task
        annexRepository.getByTaskId(task.id!!).collect { list ->
            list.forEach { annex ->
                _selectedAnnexes.value?.add(annex)
            }
        }
    }

    fun saveAnnexes() = viewModelScope.launch {
        _selectedAnnexes.value?.forEach { annex ->
            annexRepository.insert(annex)
            saveFileToExternalStorage(Uri.parse(annex.sourcePath))
        }
    }

    fun configureSelectFileLauncher(fragment: Fragment) {
        filerLauncher = fragment.registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedFileUri = data?.data
                selectedFileUri?.let {
                    _selectedAnnexes.value?.add(
                        Annex(it.lastPathSegment, it.path, selectedTask.value?.id)
                    )
                }
            }
        }
    }

    fun selectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        filerLauncher?.launch(intent)
    }

    private fun saveFileToExternalStorage(uri: Uri) {
        try {
            val sourceFile = File(uri.path!!)
            val destinationFilename = android.os.Environment.getExternalStorageDirectory().path +
                    File.separator +
                    Utils.ATTACHMENTS_DIRECTORY +
                    File.separator +
                    uri.lastPathSegment
            val destinationFile = File(destinationFilename)

            val sourceChannel = FileInputStream(sourceFile).channel
            val destinationChannel = FileOutputStream(destinationFile).channel
            destinationChannel.transferFrom(sourceChannel, 0, sourceChannel.size())
            sourceChannel.close()
            destinationChannel.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}