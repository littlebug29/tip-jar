package com.example.tipjar

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tipjar.database.TipDatabase
import com.example.tipjar.database.entity.TipHistory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TipHistoryRepositoryTest {
    private lateinit var database: TipDatabase
    private lateinit var repository: TipHistoryRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            TipDatabase::class.java
        ).allowMainThreadQueries().build()
        val dao = database.tipHistoryDao()
        repository = TipHistoryRepository(dao)
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertTipHistory() = runBlocking {
        val tipHistory =
            TipHistory(timestamp = System.currentTimeMillis(), amount = 100.0, tip = 15.0)
        repository.insert(tipHistory)

        val allTipHistories = repository.allTipHistories.first()
        assertEquals(1, allTipHistories.size)
        assertTrue(allTipHistories.contains(tipHistory))
    }

    @Test
    fun getAllTipHistories() = runBlocking {
        val tipHistory1 =
            TipHistory(timestamp = System.currentTimeMillis(), amount = 100.0, tip = 15.0)
        val tipHistory2 =
            TipHistory(timestamp = System.currentTimeMillis() + 1, amount = 200.0, tip = 30.0)
        repository.insert(tipHistory1)
        repository.insert(tipHistory2)

        val allTipHistories = repository.allTipHistories.first()
        assertEquals(2, allTipHistories.size)
    }
}