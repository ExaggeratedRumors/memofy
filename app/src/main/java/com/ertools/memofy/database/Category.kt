package com.ertools.memofy.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="categories")
data class Category (
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val name: String?,
    val resourceColor: Int?
)