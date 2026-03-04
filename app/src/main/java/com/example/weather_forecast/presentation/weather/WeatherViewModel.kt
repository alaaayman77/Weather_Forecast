package com.example.weather_forecast.presentation.weather

import android.location.Location
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository

import com.example.weather_forecast.data.models.WeatherResponse
import com.example.weather_forecast.utils.LocationProvider
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response


class WeatherViewModel(private val locationProvider: LocationProvider , private  val weatherRepository: WeatherRepository) : ViewModel() {

    private val _locationState = MutableStateFlow<Location>(Location(""))
    val locationState: StateFlow<Location>
        get() = _locationState
    private val _openLocationSettings = MutableSharedFlow<Unit>()
    val openLocationSettings: SharedFlow<Unit>
        get() = _openLocationSettings
    private val _weatherUiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)
    val weatherUiState: StateFlow<WeatherUiState>
        get() = _weatherUiState
    fun checkLocationAndFetch() {
        if (locationProvider.isLocationEnabled()) {
            locationProvider.getFreshLocation { location ->
                _locationState.value = location
                fetchWeather(location.latitude, location.longitude)
            }
        } else {
            viewModelScope.launch {
                _openLocationSettings.emit(Unit)
            }
        }
    }

    private fun fetchWeather(lat: Double, lon: Double, apiKey: String = "3ec08632a7a945e6408e9414cd1fab66") {
        viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            try {

                val currentDeferred = async { weatherRepository.getCurrentWeather(lat, lon, apiKey) }
                val hourlyDeferred  = async { weatherRepository.getHourlyForecast(lat, lon, apiKey) }

                val currentResponse = currentDeferred.await()
                val hourlyResponse  = hourlyDeferred.await()

                if (currentResponse.isSuccessful && hourlyResponse.isSuccessful) {
                    val current = currentResponse.body()
                    val hourly  = hourlyResponse.body()
                    if (current != null && hourly != null) {
                        _weatherUiState.value = WeatherUiState.Success(
                            weather = current,
                            hourly  = hourly.list
                        )
                    } else {
                        _weatherUiState.value = WeatherUiState.Error("Empty response")
                    }
                } else {
                    _weatherUiState.value = WeatherUiState.Error("Error fetching weather")
                }
            } catch (ex: Exception) {
                _weatherUiState.value = WeatherUiState.Error(ex.message ?: "Unknown error")
            }
        }
    }

}
class WeatherViewModelFactory(private val locationProvider: LocationProvider ,private  val weatherRepository: WeatherRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(locationProvider , weatherRepository) as T
    }
}


