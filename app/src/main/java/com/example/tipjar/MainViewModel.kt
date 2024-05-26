package com.example.tipjar

import android.app.Application
import android.icu.text.DecimalFormat
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tipjar.database.entity.TipHistory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val repository: TipHistoryRepository,
    private val currentTimeInMillis: () -> Long
) : AndroidViewModel(application) {
    private val moneyFormat = DecimalFormat("#,###.##")

    var amount = mutableStateOf("")
    var people = mutableIntStateOf(0)
    var tipPercent = mutableIntStateOf(0)
    private var totalTip = derivedStateOf {
        val amountDouble: Double = amount.value.toDoubleOrNull() ?: 0.0
        amountDouble * tipPercent.intValue / 100
    }
    val totalTipString = derivedStateOf { moneyFormat.format(totalTip.value) }
    private var perPerson = derivedStateOf {
        val people = people.intValue
        if (people == 0) 0.0 else totalTip.value / people
    }
    val perPersonString = derivedStateOf { moneyFormat.format(perPerson.value) }
    val savedPayments = repository.allTipHistories

    fun savePayment(photoUri: String?) {
        val timestamp = currentTimeInMillis()
        val tipHistory = TipHistory(
            timestamp = timestamp,
            amount = amount.value.toDoubleOrNull() ?: 0.0,
            tip = totalTip.value,
            photoUri = photoUri
        )
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(tipHistory)
        }
    }
}
