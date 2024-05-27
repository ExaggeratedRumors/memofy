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
    @Query("select * from tasks order by finishedAt asc")
    fun select(): Flow<List<Task>>
    @Query("select count(*) from tasks")
    fun count(): Int
    @Query("select * from tasks where category = :category order by finishedAt asc")
    fun selectByCategory(category: String): Flow<List<Task>>
    @Query("select * from tasks where completed = :completed order by finishedAt asc")
    fun selectByStatus(completed: Boolean): Flow<List<Task>>
    @Query("select * from tasks where completed = :completed and category = :category order by finishedAt asc")
    fun selectByStatusAndCategory(completed: Boolean, category: String): Flow<List<Task>>
}