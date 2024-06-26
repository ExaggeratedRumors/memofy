package com.ertools.memofy.storage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.OpenableColumns
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.ertools.memofy.R
import com.ertools.memofy.database.annexes.Annex
import com.ertools.memofy.ui.task.TaskViewModel
import com.ertools.memofy.utils.BitmapConverter
import com.ertools.memofy.utils.Utils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream


class AnnexFileManager {
    private var fileLauncher: ActivityResultLauncher<Intent>? = null
    fun configureSelectFileLauncher(fragment: Fragment, taskViewModel: TaskViewModel) {
        fileLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                val icon: Bitmap = if(selectedFileUri.toString().contains("/image%")) {
                    fragment.requireActivity().contentResolver.loadThumbnail(
                        selectedFileUri, android.util.Size(100, 100), null
                    )
                } else {
                    BitmapFactory.decodeResource(fragment.requireContext().resources, R.drawable.logo)
                }

                val thumbnail = BitmapConverter.bitmapToString(icon)

                /** Paths **/
                val destinationPath = Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .path +
                        File.separator +
                        Utils.ATTACHMENTS_DIRECTORY +
                        File.separator +
                        taskViewModel.selectedTask.value?.id +
                        File.separator

                /** Update annexes **/
                val newAnnex = Annex(filename, destinationPath, taskViewModel.selectedTask.value?.id, thumbnail)
                newAnnex.sourceUri = selectedFileUri
                taskViewModel.addAnnex(newAnnex)
            }
        }
    }

    fun selectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        fileLauncher?.launch(intent)
    }

    fun saveFileToExternalStorage(activity: Activity, annex: Annex) {
        try {
            if(annex.currentPath == null || annex.sourceUri == null || annex.name == null) return
            val destinationPath = File(annex.currentPath)
            if(!destinationPath.exists()) destinationPath.mkdirs()
            val destinationFile = File(annex.currentPath, annex.name)
            val sourceStream: InputStream? = activity
                .contentResolver
                .openInputStream(annex.sourceUri!!)
            sourceStream?.use { input ->
                FileOutputStream(destinationFile).use { output -> input.copyTo(output) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun deleteFromExternalStorage(annex: Annex) {
        try {
            if(annex.currentPath == null || annex.name == null) return
            val file = File(annex.currentPath, annex.name)
            if(file.exists()) file.delete()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}