package com.ertools.memofy.model.categories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)
    @Query("delete from categories where id = :id")
    suspend fun delete(id: Int)
    @Update
    suspend fun update(category: Category)
    @Query("delete from categories")
    suspend fun clear()
    @Query("select * from categories")
    fun select(): Flow<List<Category>>
    @Query("select * from categories where id = :id")
    fun selectById(id: Int): Flow<Category>
    @Transaction
    @Query("select * from categories where name = :name")
    fun selectByName(name: String): Flow<Category>

    @Query("select count(*) from categories")
    fun count(): Int
}