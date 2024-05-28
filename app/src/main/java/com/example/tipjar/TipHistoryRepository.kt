package com.example.tipjar

import com.example.tipjar.database.dao.TipHistoryDao
import com.example.tipjar.database.entity.TipHistory
import kotlinx.coroutines.flow.Flow

class TipHistoryRepository(
    private val tipHistoryDao: TipHistoryDao
) {
    val allTipHistories: Flow<List<TipHistory>> = tipHistoryDao.getAllTipHistories()

    suspend fun saveTip(tipHistory: TipHistory) {
        tipHistoryDao.insert(tipHistory)
    }

    suspend fun searchTipHistories(startTime: Long, endTime: Long): Flow<List<TipHistory>> =
        tipHistoryDao.searchTipHistories(startTime, endTime)
}