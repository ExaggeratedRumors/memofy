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

class TaskViewModel : ViewModel() {
    private val _selectedFileUri = MutableLiveData<Uri?>()
    val selectedFileUri: LiveData<Uri?> = _selectedFileUri
    private var selectFileLauncher: ActivityResultLauncher<Intent>? = null

    fun configureSelectFileLauncher(fragment: Fragment) {
        selectFileLauncher = fragment.registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedFileUri = data?.data
                selectedFileUri?.let {
                    _selectedFileUri.value = it
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
}