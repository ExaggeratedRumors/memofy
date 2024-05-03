package com.ertools.memofy.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDAO {
    @Insert
    fun insert(task: Task)
    @Query("delete from tasks where id = :id")
    fun delete(id: Int)
    @Update
    fun update(task: Task)
    @Query("select * from tasks")
    fun display(): List<Task>
}