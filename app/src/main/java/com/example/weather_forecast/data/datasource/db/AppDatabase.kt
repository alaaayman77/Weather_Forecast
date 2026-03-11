package com.example.weather_forecast.data.datasource.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.weather_forecast.data.datasource.local.AlertsDao
import com.example.weather_forecast.data.datasource.local.FavouritesDao
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.utils.AlertsTypeConvertors
import com.example.weather_forecast.utils.WeatherTypeConverters

@Database(entities = [FavouriteEntity::class, AlertEntity::class], version = 1)
@TypeConverters(WeatherTypeConverters::class , AlertsTypeConvertors::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDao(): FavouritesDao
    abstract fun alertsDao(): AlertsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}