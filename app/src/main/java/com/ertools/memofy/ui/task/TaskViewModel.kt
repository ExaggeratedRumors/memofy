package com.ertools.memofy.ui.task

import android.app.Activity
import android.content.Intent
import android.os.Environment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.provider.OpenableColumns
import com.ertools.memofy.database.annexes.*
import com.ertools.memofy.database.tasks.Task
import com.ertools.memofy.utils.*
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class TaskViewModel(
    private val annexRepository: AnnexRepository,
): ViewModel() {
    private val selectedTask = MutableLiveData<Task?>()
    private val _annexes: MutableLiveData<List<Annex>> by lazy {
        MutableLiveData<List<Annex>>()
    }
    val annexes: LiveData<List<Annex>> = _annexes
    private var filerLauncher: ActivityResultLauncher<Intent>? = null

    init {
        _annexes.value = ArrayList()
    }


    /** Manage annexes in application **/

    fun setTask(task: Task?) {
        selectedTask.value = task
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
            saveFileToExternalStorage(annex, fragment)
        }

        viewModelScope.launch {
            _annexes.value?.forEach { annex ->
                annexRepository.insert(annex)
            }
        }
    }

    fun deleteAnnex(annex: Annex) {
        _annexes.value = _annexes.value?.filter { it.id != annex.id }
        if(selectedTask.value != null) {
            deleteFromExternalStorage(annex)
            viewModelScope.launch {
                annexRepository.delete(annex)
            }
        }
    }

    fun cancelAnnexes() {
        _annexes.value = ArrayList()
    }

    fun deleteAnnexesFromTask() = viewModelScope.launch {
        selectedTask.value?.id?.let {
            _annexes.value?.forEach { annex ->
                deleteFromExternalStorage(annex)
            }
            annexRepository.deleteByTaskId(it)
        }
    }


    /** Manage files **/

    fun configureSelectFileLauncher(fragment: Fragment) {
        filerLauncher = fragment.registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedFileUri = data?.data

                /** File name **/
                var filename: String? = null
                fragment.requireActivity().contentResolver.query(
                    selectedFileUri!!, null, null, null, null
                )?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    filename = cursor.getString(nameIndex)
                }

                /** Thumbnail **/
                val icon = fragment.requireActivity().contentResolver.loadThumbnail(
                    selectedFileUri, android.util.Size(100, 100), null
                )
                val thumbnail = BitmapConverter.bitmapToString(icon)

                println(selectedFileUri.encodedPath)
                println(selectedFileUri.fragment)
                println(selectedFileUri.pathSegments)
                println(selectedFileUri.host)

                /** Paths **/
                val destinationPath = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .path +
                        File.separator +
                        Utils.ATTACHMENTS_DIRECTORY +
                        File.separator +
                        selectedTask.value?.id +
                        File.separator

                /** Update annexes **/
                val newAnnex = Annex(filename, destinationPath, selectedTask.value?.id, thumbnail)
                newAnnex.sourceUri = selectedFileUri
                val newAnnexes = _annexes.value?.toMutableList()
                newAnnexes?.add(newAnnex)
                _annexes.setValue(newAnnexes)
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

    private fun saveFileToExternalStorage(annex: Annex, fragment: Fragment) {
        try {
            if(annex.currentPath == null || annex.sourceUri == null || annex.name == null) return
            val destinationPath = File(annex.currentPath)
            if(!destinationPath.exists()) destinationPath.mkdirs()
            val destinationFile = File(annex.currentPath, annex.name)
            val sourceStream: InputStream? = fragment
                .requireActivity()
                .contentResolver
                .openInputStream(annex.sourceUri!!)
            sourceStream?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun deleteFromExternalStorage(annex: Annex) {
        try {
            if(annex.currentPath == null || annex.name == null) return
            val file = File(annex.currentPath, annex.name)
            if(file.exists()) file.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        }
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