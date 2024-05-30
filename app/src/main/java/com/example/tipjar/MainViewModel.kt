package com.example.tipjar

import android.app.Application
import android.icu.text.DecimalFormat
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipjar.database.entity.TipHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: TipHistoryRepository,
    private val currentTimeInMillis: () -> Long
) : AndroidViewModel(application) {
    private val moneyFormat = DecimalFormat("#,###.##")

    private var mutableAmount = mutableStateOf("")
    val amount: State<String> = mutableAmount
    private var mutablePeople = mutableIntStateOf(0)
    val people: State<Int> = mutablePeople
    private var mutableTipPercent = mutableIntStateOf(0)
    val tipPercent: State<Int> = mutableTipPercent

    private var totalTip = derivedStateOf {
        val amountDouble: Double = mutableAmount.value.toDoubleOrNull() ?: 0.0
        amountDouble * mutableTipPercent.intValue / 100
    }
    val totalTipString = derivedStateOf { moneyFormat.format(totalTip.value) }

    private var perPerson = derivedStateOf {
        val people = mutablePeople.intValue
        if (people == 0) 0.0 else totalTip.value / people
    }
    val perPersonString = derivedStateOf { moneyFormat.format(perPerson.value) }

    val savePaymentStatus = derivedStateOf {
        val amountValue = mutableAmount.value.toDoubleOrNull() ?: 0.0
        amountValue > 0
    }

    private val mutableTipHistories = mutableStateOf<List<TipHistory>>(emptyList())
    val tipHistories: State<List<TipHistory>> = mutableTipHistories

    private val mutableSelectedTipHistory = MutableStateFlow<TipHistory?>(null)
    val selectedTipHistory: StateFlow<TipHistory?> = mutableSelectedTipHistory

    fun updateAmount(newAmount: String) {
        mutableAmount.value = cleanAmountInput(newAmount)
    }

    private fun cleanAmountInput(input: String): String =
        input.replace(Regex("[^\\d.]"), "")

    fun updateTipPercent(newPercentage: String) {
        mutableTipPercent.intValue = newPercentage.toIntOrNull() ?: 0
    }

    fun updatePeople(isIncreasing: Boolean) {
        if (isIncreasing) {
            mutablePeople.intValue++
        } else {
            if (mutablePeople.intValue >= 1) mutablePeople.intValue--
        }
    }

    fun loadAllTipHistories() {
        viewModelScope.launch {
            repository.getAllTipHistories().collect { allTipHistories ->
                mutableTipHistories.value = allTipHistories
            }
        }
    }

    fun savePayment(photoUri: String?) {
        val timestamp = currentTimeInMillis()
        val tipHistory = TipHistory(
            timestamp = timestamp,
            amount = mutableAmount.value.toDoubleOrNull() ?: 0.0,
            tip = totalTip.value,
            photoUri = photoUri
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.saveTip(tipHistory)
        }
    }


    fun selectTipHistory(tipHistory: TipHistory) {
        mutableSelectedTipHistory.value = tipHistory
    }

    fun searchTipHistories(startTime: Long, endTime: Long) {
        viewModelScope.launch {
            repository.searchTipHistories(startTime, endTime).collect { histories ->
                mutableTipHistories.value = histories
            }
        }
    }

    fun deleteTipHistory(tipHistory: TipHistory) {
        viewModelScope.launch {
            repository.deleteTipHistory(tipHistory)
        }
    }
}
