package com.ertools.memofy.ui.task

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.ertools.memofy.model.annexes.Annex
import com.ertools.memofy.model.annexes.AnnexRepository
import com.ertools.memofy.model.tasks.Task
import com.ertools.memofy.model.tasks.TaskRepository
import com.ertools.memofy.utils.Utils
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class TaskViewModel(
    private val annexRepository: AnnexRepository,
): ViewModel() {
    private val selectedTask = MutableLiveData<Task?>()
    private val _selectedAnnexes =  selectedTask.switchMap {
        if(it == null) MutableLiveData()
        else annexRepository.getByTaskId(it.id!!).asLiveData()
    }
    val selectedAnnexes: LiveData<List<Annex>> = _selectedAnnexes
    private val _selectedFileUri = MutableLiveData<Uri?>()
    val selectedFileUri: LiveData<Uri?> = _selectedFileUri
    private var selectFileLauncher: ActivityResultLauncher<Intent>? = null

    fun setTask(task: Task) {
        selectedTask.value = task
    }

    fun configureSelectFileLauncher(fragment: Fragment) {
        selectFileLauncher = fragment.registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedFileUri = data?.data
                selectedFileUri?.let {
                    _selectedFileUri.value = it
                    saveFileToExternalStorage(it)
                }
            }
        }
    }

    fun selectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        selectFileLauncher?.launch(intent)
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