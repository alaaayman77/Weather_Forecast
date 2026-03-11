package com.example.weather_forecast.data.datasource.local

import android.content.Context
import com.example.weather_forecast.data.datasource.db.AppDatabase
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.FavouriteEntity
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSource(context: Context) {
    private val favouritesDao: FavouritesDao = AppDatabase.getInstance(context).favouriteDao()
    private val alertsDao : AlertsDao = AppDatabase.getInstance(context).alertsDao()



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