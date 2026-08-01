package com.example.nutritrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_logs")
data class FoodLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val calories: Double,
    val protein: Double,
    val fat: Double,
    val fiber: Double,
    val sodium: Double,
    val servingSize: Double,
    val servingUnit: String,
    val dateLogged: Long
)
