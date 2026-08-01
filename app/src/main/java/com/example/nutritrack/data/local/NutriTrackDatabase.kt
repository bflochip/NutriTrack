package com.example.nutritrack.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.nutritrack.data.local.entity.FoodLogEntry
import com.example.nutritrack.data.local.entity.WaterLogEntry
import com.example.nutritrack.data.local.entity.WeightLogEntry
import com.example.nutritrack.data.local.dao.NutriTrackDao

@Database(entities = [FoodLogEntry::class, WaterLogEntry::class, WeightLogEntry::class], version = 1, exportSchema = false)
abstract class NutriTrackDatabase : RoomDatabase() {
    abstract fun dao(): NutriTrackDao

    companion object {
        @Volatile
        private var INSTANCE: NutriTrackDatabase? = null

        fun getDatabase(context: Context): NutriTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NutriTrackDatabase::class.java,
                    "nutritrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
