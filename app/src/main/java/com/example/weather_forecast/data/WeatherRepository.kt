package com.example.weather_forecast.data

import android.content.Context
import com.example.weather_forecast.data.datasource.local.WeatherLocalDataSource
import com.example.weather_forecast.data.datasource.remote.WeatherRemoteDataSource
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.OneCallResponse
import com.example.weather_forecast.data.models.WeatherAlert
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class WeatherRepository (context: Context){
    private val remoteDataSource = WeatherRemoteDataSource()
    private val localDataSource = WeatherLocalDataSource(context)
    suspend fun getOneCallResponse(lat: Double, lon: Double, apiKey: String): Response<OneCallResponse> {
        return remoteDataSource.getOneCallResponse(lat, lon, apiKey)
    }
    fun getAllFavourites(): Flow<List<FavouriteEntity>> {
        return localDataSource.getAllFavourites()
    }

    suspend fun addFavourite(item: FavouriteEntity) {
        return localDataSource.addFavourite(item)}

    suspend fun removeFavourite(lat: Double, lon: Double) {
        return localDataSource.removeFavourite(lat, lon)}

    suspend fun getCachedWeather(lat: Double, lon: Double): FavouriteEntity? {
        return localDataSource.getCachedWeather(lat, lon)
    }
    suspend fun getWeatherAlerts(lat: Double, lon: Double, apiKey: String
    ): Result<List<WeatherAlert>> {
        return try {
            val response = remoteDataSource.getWeatherAlerts(lat, lon, apiKey)
            if (response.isSuccessful) {
                Result.success(response.body()?.alerts ?: emptyList())
            } else {
                Result.failure(Exception("API error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllAlerts(): Flow<List<AlertEntity>>{
        return localDataSource.getAllAlerts()

    }
    suspend fun insertAlert(alert: AlertEntity){
        return localDataSource.insertAlert(alert)
    }
    suspend fun updateStatus(alertId: Int, status: AlertStatus){
        return localDataSource.updateStatus(alertId, status)
    }
    suspend fun deleteAlert(alertId: Int){
        return localDataSource.deleteAlert(alertId)
    }
}
