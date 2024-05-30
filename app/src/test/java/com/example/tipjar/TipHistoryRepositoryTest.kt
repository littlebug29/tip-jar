package com.example.tipjar

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tipjar.database.dao.TipHistoryDao
import com.example.tipjar.database.entity.TipHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.quality.Strictness

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class TipHistoryRepositoryTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS)

    @Mock
    private lateinit var tipHistoryDao: TipHistoryDao

    private lateinit var tipHistoryRepository: TipHistoryRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        tipHistoryRepository = TipHistoryRepository(tipHistoryDao, testDispatcher)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun saveTip() = runTest {
        val tipHistory = TipHistory(timestamp = System.currentTimeMillis(), amount = 100.0, tip = 10.0, photoUri = "test_uri")

        tipHistoryRepository.saveTip(tipHistory)

        verify(tipHistoryDao).insert(tipHistory)
    }

    @Test
    fun getAllTipHistories() = runTest {
        val tipHistories = listOf(
            TipHistory(timestamp = System.currentTimeMillis(), amount = 100.0, tip = 10.0, photoUri = "test_uri1"),
            TipHistory(timestamp = System.currentTimeMillis(), amount = 200.0, tip = 20.0, photoUri = "test_uri2")
        )
        `when`(tipHistoryDao.getAllTipHistories()).thenReturn(flowOf(tipHistories))

        val result = tipHistoryRepository.getAllTipHistories().toList()

        verify(tipHistoryDao).getAllTipHistories()
        assertEquals(tipHistories, result.get(0))
    }

    @Test
    fun searchTipHistories() = runTest {
        val startTime = 1622505600000 // Example start time
        val endTime = 1625097600000 // Example end time
        val tipHistories = listOf(
            TipHistory(timestamp = System.currentTimeMillis(), amount = 100.0, tip = 10.0, photoUri = "test_uri1"),
            TipHistory(timestamp = System.currentTimeMillis(), amount = 200.0, tip = 20.0, photoUri = "test_uri2")
        )
        `when`(tipHistoryDao.searchTipHistories(startTime, endTime)).thenReturn(flowOf(tipHistories))

        val result = tipHistoryRepository.searchTipHistories(startTime, endTime).first()

        verify(tipHistoryDao).searchTipHistories(startTime, endTime)
        assertEquals(tipHistories, result)
    }

    @Test
    fun deleteTipHistory() = runTest {
        val tipHistory = TipHistory(timestamp = System.currentTimeMillis(), amount = 100.0, tip = 10.0, photoUri = "test_uri")

        tipHistoryRepository.deleteTipHistory(tipHistory)

        verify(tipHistoryDao).delete(tipHistory)
    }
}