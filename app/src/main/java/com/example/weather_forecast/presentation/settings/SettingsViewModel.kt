package com.example.weather_forecast.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
class SettingsViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _tempUnit = MutableStateFlow(repository.getTempUnit())
    val tempUnit: StateFlow<TempUnit>
        get() = _tempUnit

    private val _windUnit = MutableStateFlow(repository.getWindUnit())
    val windUnit: StateFlow<WindUnit>
        get() = _windUnit

    private val _locationSource = MutableStateFlow(repository.getLocationSource())
    val locationSource: StateFlow<LocationSource>
        get() = _locationSource

    private val _language = MutableStateFlow(repository.getLanguage())
    val language: StateFlow<String>
        get() = _language

    fun setTempUnit(unit: TempUnit) {
        repository.saveTempUnit(unit) //save to prefs
        _tempUnit.value = unit
    }

    fun setWindUnit(unit: WindUnit) {
        repository.saveWindUnit(unit)
        _windUnit.value = unit
    }

    fun setLocationSource(src: LocationSource) {
        repository.saveLocationSource(src)
        _locationSource.value = src
    }

    fun setLanguage(lang: String) {
        repository.saveLanguage(lang)
        _language.value = lang
    }
    fun saveManualLocation(lat: Double, lon: Double) {
        repository.saveManualLocation(lat, lon)
    }
}

class SettingsViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        SettingsViewModel(repository) as T
}