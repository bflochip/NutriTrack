package com.example.nutritrack.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountOz: Double,
    val dateLogged: Long
)
