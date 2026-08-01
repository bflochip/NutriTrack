package com.example.nutritrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.local.dao.NutriTrackDao
import com.example.nutritrack.data.local.entity.FoodLogEntry
import com.example.nutritrack.data.remote.RetrofitClient
import com.example.nutritrack.data.remote.dto.FoodItemDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val dao: NutriTrackDao) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<FoodItemDto>>(emptyList())
    val searchResults: StateFlow<List<FoodItemDto>> = _searchResults

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun searchFoods(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.api.searchFoods(query)
                _searchResults.value = response.foods
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logFood(food: FoodItemDto, servingSize: Double, servingUnit: String) {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val nutrients = food.foodNutrients
            val calories = nutrients.find { it.nutrientName.contains("Energy") }?.value ?: 0.0
            val protein = nutrients.find { it.nutrientName.contains("Protein") }?.value ?: 0.0
            val fat = nutrients.find { it.nutrientName.contains("Total lipid (fat)") }?.value ?: 0.0
            val fiber = nutrients.find { it.nutrientName.contains("Fiber") }?.value ?: 0.0
            val sodium = nutrients.find { it.nutrientName.contains("Sodium") }?.value ?: 0.0

            val multiplier = servingSize / 100.0

            val entry = FoodLogEntry(
                name = food.description,
                calories = calories * multiplier,
                protein = protein * multiplier,
                fat = fat * multiplier,
                fiber = fiber * multiplier,
                sodium = sodium * multiplier,
                servingSize = servingSize,
                servingUnit = servingUnit,
                dateLogged = System.currentTimeMillis()
            )
            dao.insertFoodLog(entry)
        }
    }
}
