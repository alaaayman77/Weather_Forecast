package com.example.weather_forecast.data

import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import kotlinx.coroutines.flow.Flow

interface LocalDataSource {
    fun saveTempUnit(unit: TempUnit)
    fun saveWindUnit(unit: WindUnit)
    fun saveLocationSource(src: LocationSource)
    fun saveManualLocation(lat: Double, lon: Double)
    fun saveLanguage(lang: Language)
    fun getTempUnit(): TempUnit
    fun getWindUnit(): WindUnit
    fun getLocationSource(): LocationSource
    fun getManualLocation(): Pair<Double, Double>?
    fun getLanguage(): Language

    suspend fun addFavourite(item: FavouriteEntity)

    suspend fun removeFavourite(lat: Double, lon: Double)

    fun getAllFavourites(): Flow<List<FavouriteEntity>>
    suspend fun getCachedWeather(lat: Double, lon: Double): FavouriteEntity?
    fun getAllAlerts(): Flow<List<AlertEntity>>
    suspend fun insertAlert(alert: AlertEntity)
    suspend fun updateStatus(alertId: Int, status: AlertStatus)
    suspend fun deleteAlert(alertId: Int)
}