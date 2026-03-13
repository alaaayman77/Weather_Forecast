package com.example.weather_forecast.data.datasource.local

import android.content.Context
import com.example.weather_forecast.data.datasource.db.AppDatabase
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(context: Context) {
    private val favouritesDao: FavouritesDao = AppDatabase.getInstance(context).favouriteDao()
    private val alertsDao : AlertsDao = AppDatabase.getInstance(context).alertsDao()
    private val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)


    fun saveTempUnit(unit: TempUnit) = prefs.edit().putString("temp_unit", unit.name).apply()
    fun saveWindUnit(unit: WindUnit) = prefs.edit().putString("wind_unit", unit.name).apply()
    fun saveLocationSource(src: LocationSource) = prefs.edit().putString("location_src", src.name).apply()

    fun saveManualLocation(lat: Double, lon: Double) {
        prefs.edit()
            .putFloat("manual_lat", lat.toFloat())
            .putFloat("manual_lon", lon.toFloat())
            .apply()
    }


    fun saveLanguage(lang: Language) = prefs.edit().putString("language", lang.name).apply()

    fun getTempUnit(): TempUnit = TempUnit.valueOf(prefs.getString("temp_unit", TempUnit.CELSIUS.name)!!)
    fun getWindUnit(): WindUnit = WindUnit.valueOf(prefs.getString("wind_unit", WindUnit.MS.name)!!)
    fun getLocationSource(): LocationSource = LocationSource.valueOf(prefs.getString("location_src", LocationSource.GPS.name)!!)
    fun getManualLocation(): Pair<Double, Double>? {
        val lat = prefs.getFloat("manual_lat", Float.MIN_VALUE)
        val lon = prefs.getFloat("manual_lon", Float.MIN_VALUE)
        return if (lat == Float.MIN_VALUE) null else Pair(lat.toDouble(), lon.toDouble())
    }
    fun getLanguage(): Language = Language.valueOf(prefs.getString("language", Language.ENGLISH.name)!!)




    fun getAllFavourites(): Flow<List<FavouriteEntity>> {
        return favouritesDao.getAllFavourites()
    }

    suspend fun addFavourite(item: FavouriteEntity) {
        return favouritesDao.insertFavourite(item)}

    suspend fun removeFavourite(lat: Double, lon: Double) {
       return favouritesDao.deleteFavouriteByLatLon(lat, lon)}

    suspend fun getCachedWeather(lat: Double, lon: Double): FavouriteEntity? {
        return favouritesDao.getFavouriteByLatLon(lat, lon)
    }
    fun getAllAlerts(): Flow<List<AlertEntity>>{
        return alertsDao.getAllAlerts()

    }
    suspend fun insertAlert(alert: AlertEntity){
        return alertsDao.insertAlert(alert)
    }
    suspend fun updateStatus(alertId: Int, status: AlertStatus){
        return alertsDao.updateStatus(alertId, status)
    }
    suspend fun deleteAlert(alertId: Int){
        return alertsDao.deleteAlert(alertId)
    }

}