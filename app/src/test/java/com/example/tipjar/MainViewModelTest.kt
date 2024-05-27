package com.example.tipjar

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tipjar.database.entity.TipHistory
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {
    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    private lateinit var viewModel: MainViewModel

    @Mock
    private lateinit var repository: TipHistoryRepository

    private val testDispatcher = UnconfinedTestDispatcher()
    private val fixedTimestamp = 1653302130000L // Fixed timestamp for testing

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        `when`(repository.allTipHistories).thenReturn(flowOf(emptyList()))
        viewModel = MainViewModel(getApplicationContext(), repository) { fixedTimestamp }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun savePayment_addsPaymentToRepository() = runTest {
        viewModel.amount.value = "200.0"
        viewModel.tipPercent.intValue = 10
        viewModel.people.intValue = 4

        viewModel.savePayment(null)

        val expectedTipHistory = TipHistory(
            timestamp = fixedTimestamp,
            amount = 200.0,
            tip = 20.0,
            photoUri = null
        )

        verify(repository).saveTip(expectedTipHistory)
    }

    @Test
    fun loadTipHistories_initiallyEmpty() = runTest {
        val historyList = viewModel.savedPayments.lastOrNull()
        assertThat(historyList).isEmpty()
    }

    @Test
    fun loadTipHistories_updatesWithNewEntries() = runTest {
        val tipHistory1 = TipHistory(
            timestamp = fixedTimestamp,
            amount = 50.0,
            tip = 5.0,
            photoUri = null
        )
        val tipHistory2 = TipHistory(
            timestamp = fixedTimestamp + 1,
            amount = 100.0,
            tip = 10.0,
            photoUri = null
        )
        `when`(repository.allTipHistories).thenReturn(flowOf(listOf(tipHistory1, tipHistory2)))

        viewModel = MainViewModel(getApplicationContext(), repository) { fixedTimestamp }

        assertThat(viewModel.savedPayments.last()).containsExactly(tipHistory1, tipHistory2)
    }
}