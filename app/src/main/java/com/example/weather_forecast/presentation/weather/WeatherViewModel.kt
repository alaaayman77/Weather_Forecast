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
import kotlinx.coroutines.launch
import retrofit2.Response


class WeatherViewModel(private val locationProvider: LocationProvider , private  val weatherRepository: WeatherRepository) : ViewModel() {

    private val _locationState = MutableLiveData<Location>()
    val locationState: LiveData<Location>
        get() = _locationState
    private val _weatherUiState = MutableLiveData<WeatherUiState>(WeatherUiState.Idle)
    val weatherUiState: LiveData<WeatherUiState>
        get() = _weatherUiState
    fun checkLocationAndFetch() {
        if (locationProvider.isLocationEnabled()) {
            locationProvider.getFreshLocation { location ->
                _locationState.value = location
                fetchCurrentWeather(location.latitude, location.longitude)
            }
        } else {
            locationProvider.enableLocationServices()
        }
    }

    private fun fetchCurrentWeather(
        lat: Double,
        lon: Double,
        apiKey: String = "3ec08632a7a945e6408e9414cd1fab66"
    ) {
        viewModelScope.launch {
            _weatherUiState.value = WeatherUiState.Loading
            try {
                val response = weatherRepository.getCurrentWeather(lat, lon, apiKey)
                Log.d("WeatherDebug", "isSuccessful: ${response.isSuccessful}")
                Log.d("WeatherDebug", "code: ${response.code()}")
                Log.d("WeatherDebug", "errorBody: ${response.errorBody()?.string()}")
                Log.d("WeatherDebug", "body: ${response.body()}")
                Log.d("WeatherDebug", "name: ${response.body()?.name}")
                Log.d("WeatherDebug", "temp: ${response.body()?.main?.temp}")
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                    _weatherUiState.value = WeatherUiState.Success(body)
                    }
                    else {
                        _weatherUiState.value = WeatherUiState.Error("Empty response")
                    }
                } else {
                    _weatherUiState.value = WeatherUiState.Error("Error ${response.code()}: ${response.message()}")
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


