package com.example.nutritrack.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FoodSearchResponseDto(
    val foods: List<FoodItemDto> = emptyList()
)

@Serializable
data class FoodItemDto(
    val fdcId: Long,
    val description: String,
    val foodNutrients: List<FoodNutrientDto> = emptyList()
)

@Serializable
data class FoodNutrientDto(
    val nutrientId: Long,
    val nutrientName: String,
    val value: Double = 0.0,
    val unitName: String = ""
)
