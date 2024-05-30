package com.example.tipjar

import com.example.tipjar.database.dao.TipHistoryDao
import com.example.tipjar.database.entity.TipHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class TipHistoryRepository(
    private val tipHistoryDao: TipHistoryDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    fun getAllTipHistories(): Flow<List<TipHistory>> =
        tipHistoryDao.getAllTipHistories().flowOn(ioDispatcher)

    suspend fun saveTip(tipHistory: TipHistory) {
        tipHistoryDao.insert(tipHistory)
    }

    fun searchTipHistories(startTime: Long, endTime: Long): Flow<List<TipHistory>> =
        tipHistoryDao.searchTipHistories(startTime, endTime).flowOn(ioDispatcher)

    suspend fun deleteTipHistory(tipHistory: TipHistory) = withContext(ioDispatcher) {
        tipHistoryDao.delete(tipHistory)
    }
}