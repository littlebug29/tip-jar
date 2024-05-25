package com.example.tipjar

import com.example.tipjar.database.dao.TipHistoryDao
import com.example.tipjar.database.entity.TipHistory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TipHistoryRepository @Inject constructor(
    private val tipHistoryDao: TipHistoryDao
) {
    val allTipHistories: Flow<List<TipHistory>> = tipHistoryDao.getAllTipHistories()

    suspend fun insert(tipHistory: TipHistory) {
        tipHistoryDao.insert(tipHistory)
    }
}