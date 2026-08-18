package com.example.nutritrack.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nutritrack.data.local.dao.NutriTrackDao
import com.example.nutritrack.data.local.entity.FoodLogEntry
import com.example.nutritrack.data.remote.RetrofitClient
import com.example.nutritrack.data.remote.dto.FoodItemDto
import com.example.nutritrack.data.remote.dto.FoodNutrientDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val dao: NutriTrackDao) : ViewModel() {

    private val _searchResults = MutableStateFlow<List<FoodItemDto>>(emptyList())
    val searchResults: StateFlow<List<FoodItemDto>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun searchFoods(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitClient.api.searchFoods(query)
                _searchResults.value = response.foods
                if (response.foods.isEmpty()) {
                    _errorMessage.value = "No foods found for \"$query\"."
                }
            } catch (e: Exception) {
                // Surface the failure instead of silently leaving an empty list on screen.
                _searchResults.value = emptyList()
                _errorMessage.value = e.message ?: "Search failed. Check your connection and try again."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun logFood(food: FoodItemDto, servingSize: Double, servingUnit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val nutrients = food.foodNutrients

            // USDA reports values per 100 g of the food.
            val multiplier = servingSize / 100.0

            val entry = FoodLogEntry(
                name = food.description,
                calories = nutrients.valueOf(NutrientIds.ENERGY_KCAL) * multiplier,
                protein = nutrients.valueOf(NutrientIds.PROTEIN) * multiplier,
                fat = nutrients.valueOf(NutrientIds.FAT) * multiplier,
                fiber = nutrients.valueOf(NutrientIds.FIBER) * multiplier,
                sodium = nutrients.valueOf(NutrientIds.SODIUM) * multiplier,
                servingSize = servingSize,
                servingUnit = servingUnit,
                dateLogged = System.currentTimeMillis()
            )
            dao.insertFoodLog(entry)
        }
    }
}

/**
 * FoodData Central nutrient numbers. Matching on these is unambiguous, unlike matching on
 * [FoodNutrientDto.nutrientName] — a food carries both "Energy" in KCAL (1008) and "Energy" in
 * kJ (1062), so a name-only lookup can pick the kJ row and log roughly 4x the real calories.
 */
private object NutrientIds {
    const val ENERGY_KCAL = 1008L
    const val ENERGY_KJ = 1062L
    const val PROTEIN = 1003L
    const val FAT = 1004L
    const val FIBER = 1079L
    const val SODIUM = 1093L
}

/** Value for [nutrientId], or 0.0 when the food does not report that nutrient. */
private fun List<FoodNutrientDto>.valueOf(nutrientId: Long): Double {
    find { it.nutrientId == nutrientId }?.let { return it.value }

    // Some search results omit kcal and only carry kilojoules; convert rather than report zero.
    if (nutrientId == NutrientIds.ENERGY_KCAL) {
        find { it.nutrientId == NutrientIds.ENERGY_KJ }?.let { return it.value / KJ_PER_KCAL }
    }
    return 0.0
}

private const val KJ_PER_KCAL = 4.184
