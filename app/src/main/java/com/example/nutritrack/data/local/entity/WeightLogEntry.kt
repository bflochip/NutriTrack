package com.example.nutritrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weight_logs")
data class WeightLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightLbs: Double,
    val dateLogged: Long
)
