package com.example.nutritrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.nutritrack.data.local.entity.FoodLogEntry
import com.example.nutritrack.data.local.entity.WaterLogEntry
import com.example.nutritrack.data.local.entity.WeightLogEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface NutriTrackDao {
    @Insert
    fun insertFoodLog(foodLog: FoodLogEntry)

    @Query("SELECT * FROM food_logs WHERE dateLogged >= :startOfDay AND dateLogged < :endOfDay")
    fun getFoodLogsForDay(startOfDay: Long, endOfDay: Long): Flow<List<FoodLogEntry>>

    @Insert
    fun insertWaterLog(waterLog: WaterLogEntry)

    @Query("SELECT SUM(amountOz) FROM water_logs WHERE dateLogged >= :startOfDay AND dateLogged < :endOfDay")
    fun getWaterIntakeForDay(startOfDay: Long, endOfDay: Long): Flow<Double?>

    @Insert
    fun insertWeightLog(weightLog: WeightLogEntry)

    @Query("SELECT * FROM weight_logs ORDER BY dateLogged DESC LIMIT 1")
    fun getLatestWeight(): Flow<WeightLogEntry?>
}
