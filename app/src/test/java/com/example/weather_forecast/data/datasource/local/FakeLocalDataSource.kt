package com.example.weather_forecast.data.datasource.local

import com.example.weather_forecast.data.datasource.local.LocalDataSource
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource(
    var favourites: MutableList<FavouriteEntity>? = mutableListOf()

) : LocalDataSource {

    private var tempUnit       : TempUnit              = TempUnit.CELSIUS
    private var windUnit       : WindUnit              = WindUnit.MS
    private var locationSource : LocationSource        = LocationSource.GPS
    private var language       : Language              = Language.ENGLISH
    private var manualLocation : Pair<Double, Double>? = null

    override fun saveTempUnit(unit: TempUnit)                 { tempUnit = unit }
    override fun saveWindUnit(unit: WindUnit)                 { windUnit = unit }
    override fun saveLocationSource(src: LocationSource)      { locationSource = src }
    override fun saveLanguage(lang: Language)                 { language = lang }
    override fun saveOnboarded(done: Boolean) {
        TODO("Not yet implemented")
    }

    override fun saveManualLocation(lat: Double, lon: Double) { manualLocation = Pair(lat, lon) }

    override fun getTempUnit()       : TempUnit              = tempUnit
    override fun getWindUnit()       : WindUnit              = windUnit
    override fun getLocationSource() : LocationSource        = locationSource
    override fun getLanguage()       : Language              = language
    override fun isOnboarded(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getManualLocation() : Pair<Double, Double>? = manualLocation

    override suspend fun addFavourite(item: FavouriteEntity) {
        favourites?.add(item)
    }

    override suspend fun removeFavourite(lat: Double, lon: Double) {
        favourites?.removeIf { it.lat == lat && it.lon == lon }
    }

    override fun getAllFavourites(): Flow<List<FavouriteEntity>> {
        return flowOf(favourites ?: listOf())
    }

    override suspend fun getCachedWeather(
        lat: Double,
        lon: Double
    ): FavouriteEntity? {
        TODO("Not yet implemented")
    }

    override fun getAllAlerts(): Flow<List<AlertEntity>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertAlert(alert: AlertEntity) {
        TODO("Not yet implemented")
    }

    override suspend fun updateStatus(
        alertId: Int,
        status: AlertStatus
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAlert(alertId: Int) {
        TODO("Not yet implemented")
    }

    override fun saveCurrentWeather(json: String) {
        TODO("Not yet implemented")
    }

    override fun getCurrentWeather(): String? {
        TODO("Not yet implemented")
    }


}