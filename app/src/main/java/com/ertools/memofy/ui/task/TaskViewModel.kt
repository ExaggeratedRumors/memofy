package com.ertools.memofy.ui.task

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import android.provider.OpenableColumns
import com.ertools.memofy.model.annexes.Annex
import com.ertools.memofy.model.annexes.AnnexRepository
import com.ertools.memofy.model.tasks.Task
import com.ertools.memofy.utils.BitmapConverter
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
    private val _selectedAnnexes: MutableLiveData<List<Annex>> by lazy {
        MutableLiveData<List<Annex>>()
    }
    val annexes: LiveData<List<Annex>> = _selectedAnnexes
    private var filerLauncher: ActivityResultLauncher<Intent>? = null

    init {
        _selectedAnnexes.value = ArrayList()
    }

    fun setTask(task: Task) = viewModelScope.launch {
        selectedTask.value = task
        val annexes = ArrayList<Annex>()
        annexRepository.getByTaskId(task.id!!).collect { list ->
            list.forEach { annex ->
                annexes.add(annex)
            }
        }
        _selectedAnnexes.setValue(annexes)
    }

    fun saveAnnexes() = viewModelScope.launch {
        _selectedAnnexes.value?.forEach { annex ->
            annexRepository.insert(annex)
            println("TEST SAVE ANNEX: ${annex.name}, ${annex.sourcePath}")
            saveFileToExternalStorage(annex)
        }
    }

    fun cancelAnnex(annex: Annex) {
        _selectedAnnexes.value = _selectedAnnexes.value?.filter { it.id != annex.id }
    }

    fun configureSelectFileLauncher(fragment: Fragment) {
        filerLauncher = fragment.registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedFileUri = data?.data


                var filename: String? = null
                fragment.requireActivity().contentResolver.query(
                    selectedFileUri!!, null, null, null, null
                )?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    filename = cursor.getString(nameIndex)
                }

                val icon = fragment.requireActivity().contentResolver.loadThumbnail(
                    selectedFileUri, android.util.Size(100, 100), null
                )
                val thumbnail = BitmapConverter.bitmapToString(icon)

                val sourcePath: String? = selectedFileUri.path
                val destinationPath = android.os.Environment.getExternalStorageDirectory().path +
                        File.separator +
                        Utils.ATTACHMENTS_DIRECTORY +
                        File.separator +
                        filename

                val annexes = _selectedAnnexes.value?.toMutableList()
                annexes?.add(
                    Annex(filename, sourcePath, destinationPath, selectedTask.value?.id, thumbnail)
                )
                _selectedAnnexes.setValue(annexes)
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

    private fun saveFileToExternalStorage(annex: Annex) {
        try {
            val sourceFile = File(annex.sourcePath!!)
            val destinationFile = File(annex.currentPath!!)

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