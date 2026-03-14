package com.example.weather_forecast.data.datasource.local

import android.content.Context
import com.example.weather_forecast.data.LocalDataSource
import com.example.weather_forecast.data.datasource.db.AppDatabase
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(context: Context) : LocalDataSource {
    private val favouritesDao: FavouritesDao = AppDatabase.getInstance(context).favouriteDao()
    private val alertsDao : AlertsDao = AppDatabase.getInstance(context).alertsDao()
    private val prefs = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)


    override fun saveTempUnit(unit: TempUnit) = prefs.edit().putString("temp_unit", unit.name).apply()
    override fun saveWindUnit(unit: WindUnit) = prefs.edit().putString("wind_unit", unit.name).apply()
    override fun saveLocationSource(src: LocationSource) = prefs.edit().putString("location_src", src.name).apply()

    override fun saveManualLocation(lat: Double, lon: Double) {
        prefs.edit()
            .putFloat("manual_lat", lat.toFloat())
            .putFloat("manual_lon", lon.toFloat())
            .apply()
    }


    override fun saveLanguage(lang: Language) = prefs.edit().putString("language", lang.name).apply()

    override fun getTempUnit(): TempUnit = TempUnit.valueOf(prefs.getString("temp_unit", TempUnit.CELSIUS.name)!!)
    override fun getWindUnit(): WindUnit = WindUnit.valueOf(prefs.getString("wind_unit", WindUnit.MS.name)!!)
    override fun getLocationSource(): LocationSource = LocationSource.valueOf(prefs.getString("location_src", LocationSource.GPS.name)!!)
    override fun getManualLocation(): Pair<Double, Double>? {
        val lat = prefs.getFloat("manual_lat", Float.MIN_VALUE)
        val lon = prefs.getFloat("manual_lon", Float.MIN_VALUE)
        return if (lat == Float.MIN_VALUE) null else Pair(lat.toDouble(), lon.toDouble())
    }
    override fun getLanguage(): Language = Language.valueOf(prefs.getString("language", Language.ENGLISH.name)!!)




   override fun getAllFavourites(): Flow<List<FavouriteEntity>> {
        return favouritesDao.getAllFavourites()
    }

    override suspend fun addFavourite(item: FavouriteEntity) {
        return favouritesDao.insertFavourite(item)}

    override suspend fun removeFavourite(lat: Double, lon: Double) {
       return favouritesDao.deleteFavouriteByLatLon(lat, lon)}

    override suspend fun getCachedWeather(lat: Double, lon: Double): FavouriteEntity? {
        return favouritesDao.getFavouriteByLatLon(lat, lon)
    }
   override fun getAllAlerts(): Flow<List<AlertEntity>>{
        return alertsDao.getAllAlerts()

    }
    override suspend fun insertAlert(alert: AlertEntity){
        return alertsDao.insertAlert(alert)
    }
   override suspend fun updateStatus(alertId: Int, status: AlertStatus){
        return alertsDao.updateStatus(alertId, status)
    }
    override suspend fun deleteAlert(alertId: Int){
        return alertsDao.deleteAlert(alertId)
    }

}