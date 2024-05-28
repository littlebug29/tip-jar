package com.example.tipjar.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.tipjar.database.entity.TipHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface TipHistoryDao {
    @Insert
    suspend fun insert(tipHistory: TipHistory)

    @Query("SELECT * FROM tip_history ORDER BY timestamp DESC")
    fun getAllTipHistories(): Flow<List<TipHistory>>

    @Query("SELECT * FROM tip_history WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun searchTipHistories(startTime: Long, endTime: Long): Flow<List<TipHistory>>
}