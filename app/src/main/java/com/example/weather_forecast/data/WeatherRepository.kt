package com.example.weather_forecast.data

import android.content.Context
import com.example.weather_forecast.data.datasource.local.WeatherLocalDataSource
import com.example.weather_forecast.data.datasource.remote.WeatherRemoteDataSource
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.OneCallResponse
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

}
