package com.ertools.memofy.model.annexes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnnexDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(annex: Annex)
    @Query("delete from annexes where id = :id")
    suspend fun delete(id: Int)
    @Update
    suspend fun update(annex: Annex)
    @Query("delete from annexes")
    suspend fun clear()
    @Query("select * from annexes")
    fun select(): Flow<List<Annex>>
    @Query("select * from annexes where id = :id")
    fun selectById(id: Int): Flow<Annex>
    @Transaction
    @Query("select * from annexes where taskId = :taskId")
    fun selectByTaskId(taskId: Int): Flow<List<Annex>>
    @Query("select count(*) from annexes")
    fun count(): Int
}