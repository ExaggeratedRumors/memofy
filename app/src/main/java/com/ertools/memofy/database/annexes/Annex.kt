package com.ertools.memofy.database.annexes

import android.net.Uri
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName="annexes")
data class Annex (
    val name: String?,
    val currentPath: String?,
    var taskId: Int?,
    val thumbnail: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @Ignore
    var sourceUri: Uri? = null
}