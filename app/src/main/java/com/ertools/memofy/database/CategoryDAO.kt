package com.ertools.memofy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CategoryDAO {
    @Insert
    fun insert(category: Category)
    @Query("delete from categories where id = :id")
    fun delete(id: Int)
    @Update
    fun update(category: Category)
    @Query("select * from categories")
    fun display(): List<Category>
}