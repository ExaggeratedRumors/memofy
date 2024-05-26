package com.ertools.memofy.model.annexes

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName="annexes")
data class Annex (
    val name: String?,
    val sourcePath: String?,
    val currentPath: String?,
    val taskId: Int?,
    val thumbnail: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}