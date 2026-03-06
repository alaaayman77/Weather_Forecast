package com.example.weather_forecast.presentation.weather

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.utils.LocationProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch



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


    private fun fetchWeather(
        lat: Double,
        lon: Double,
        apiKey: String = "3ec08632a7a945e6408e9414cd1fab66"
    ) {
        viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            try {
                val response = weatherRepository.getOneCallResponse(lat, lon, apiKey)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _weatherUiState.value = WeatherUiState.Success(data = body)
                    } else {
                        _weatherUiState.value = WeatherUiState.Error("Empty response")
                    }
                } else {
                    _weatherUiState.value = WeatherUiState.Error(
                        "Error ${response.code()}: ${response.message()}"
                    )
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


