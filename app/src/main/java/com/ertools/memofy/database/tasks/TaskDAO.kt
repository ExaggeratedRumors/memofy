package com.ertools.memofy.database.tasks

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: Task)
    @Query("delete from tasks where id = :id")
    suspend fun delete(id: Int)
    @Update
    suspend fun update(task: Task)
    @Query("delete from tasks")
    suspend fun clear()
    @Query("select * from tasks")
    fun select(): Flow<List<Task>>
}