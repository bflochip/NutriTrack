package com.example.nutritrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.local.dao.NutriTrackDao
import com.example.nutritrack.data.local.entity.WaterLogEntry
import com.example.nutritrack.data.local.entity.WeightLogEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(private val dao: NutriTrackDao) : ViewModel() {

    /** Half-open [start, end) millisecond range covering the current local day. */
    private data class DayWindow(val start: Long, val end: Long)

    private val dayWindow = MutableStateFlow(currentDayWindow())

    init {
        // The window was previously computed once at construction, so a session left open past
        // midnight kept reporting the previous day's totals. Roll it over as the day changes.
        viewModelScope.launch {
            while (true) {
                delay((dayWindow.value.end - System.currentTimeMillis()).coerceAtLeast(1L))
                dayWindow.value = currentDayWindow()
            }
        }
    }

    val foodLogs = dayWindow
        .flatMapLatest { window -> dao.getFoodLogsForDay(window.start, window.end) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCalories = foodLogs.map { logs -> logs.sumOf { it.calories } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val totalProtein = foodLogs.map { logs -> logs.sumOf { it.protein } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val waterIntakeOz = dayWindow
        .flatMapLatest { window -> dao.getWaterIntakeForDay(window.start, window.end) }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val latestWeight = dao.getLatestWeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun addWater(amountOz: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertWaterLog(WaterLogEntry(amountOz = amountOz, dateLogged = System.currentTimeMillis()))
        }
    }

    fun logWeight(weightLbs: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertWeightLog(WeightLogEntry(weightLbs = weightLbs, dateLogged = System.currentTimeMillis()))
        }
    }

    private fun currentDayWindow(): DayWindow {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val start = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        return DayWindow(start, calendar.timeInMillis)
    }
}
