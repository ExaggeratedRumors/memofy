package com.ertools.memofy.database.categories

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="categories")
data class Category (
    val name: String?,
    val color: Int?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}