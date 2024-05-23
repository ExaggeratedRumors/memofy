package com.ertools.memofy.model.annexes

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class AnnexRepository(private val annexDao: AnnexDAO) {
    val annexes: Flow<List<Annex>> = annexDao.select()

    @WorkerThread
    suspend fun insert(annex: Annex) {
        annexDao.insert(annex)
    }

    @WorkerThread
    fun get(id: Int): Flow<Annex> {
        return annexDao.selectById(id)
    }

    @WorkerThread
    fun getByTaskId(id: Int): Flow<List<Annex>> {
        return annexDao.selectByTaskId(id)
    }
}