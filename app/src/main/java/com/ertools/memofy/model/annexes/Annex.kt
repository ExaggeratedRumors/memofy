package com.ertools.memofy.model.annexes

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName="annexes")
data class Annex (
    val name: String?,
    val sourcePath: String?,
    val taskId: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}