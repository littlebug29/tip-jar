package com.example.tipjar.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tipjar.database.TipDatabase
import com.example.tipjar.database.entity.TipHistory
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class TipHistoryDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: TipDatabase
    private lateinit var tipHistoryDao: TipHistoryDao
    private val fixedTimestamp = 1653302130000L // Fixed timestamp for testing

    @Before
    fun createDb() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TipDatabase::class.java
        ).allowMainThreadQueries().build()
        tipHistoryDao = database.tipHistoryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun insertTipHistoryAndReadInList() = runBlocking {
        val tipHistory = TipHistory(
            amount = 100.0,
            tip = 15.0,
            timestamp = System.currentTimeMillis(),
            photoUri = null
        )
        tipHistoryDao.insert(tipHistory)
        val allTipHistories = tipHistoryDao.getAllTipHistories().first()
        Assert.assertEquals(allTipHistories[0], tipHistory)
    }


    @Test
    fun getTipHistoryById() = runBlocking {
        val tipHistory1 = TipHistory(
            amount = 100.0,
            tip = 15.0,
            timestamp = fixedTimestamp,
            photoUri = null
        )
        val tipHistory2 = TipHistory(
            amount = 200.0,
            tip = 18.0,
            timestamp = fixedTimestamp + 1,
            photoUri = null
        )
        tipHistoryDao.insert(tipHistory1)
        tipHistoryDao.insert(tipHistory2)

        val allTipHistories = tipHistoryDao.getAllTipHistories().first()
        val retrievedTipHistory = allTipHistories.first { it.timestamp == fixedTimestamp + 1 }
        Assert.assertEquals(retrievedTipHistory, tipHistory2)
    }
}
