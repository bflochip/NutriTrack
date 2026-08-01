package com.example.nutritrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.local.dao.NutriTrackDao
import com.example.nutritrack.data.local.entity.WaterLogEntry
import com.example.nutritrack.data.local.entity.WeightLogEntry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class DashboardViewModel(private val dao: NutriTrackDao) : ViewModel() {

    private val todayStart: Long
    private val todayEnd: Long

    init {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        todayStart = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        todayEnd = calendar.timeInMillis
    }

    val foodLogs = dao.getFoodLogsForDay(todayStart, todayEnd)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCalories = foodLogs.map { logs -> logs.sumOf { it.calories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalProtein = foodLogs.map { logs -> logs.sumOf { it.protein } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val waterIntakeOz = dao.getWaterIntakeForDay(todayStart, todayEnd)
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val latestWeight = dao.getLatestWeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addWater(amountOz: Double) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            dao.insertWaterLog(WaterLogEntry(amountOz = amountOz, dateLogged = System.currentTimeMillis()))
        }
    }

    fun logWeight(weightLbs: Double) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            dao.insertWeightLog(WeightLogEntry(weightLbs = weightLbs, dateLogged = System.currentTimeMillis()))
        }
    }
}
