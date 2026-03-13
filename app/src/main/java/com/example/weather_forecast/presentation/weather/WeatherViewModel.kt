package com.example.weather_forecast.presentation.weather

import android.app.Application
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.utils.LocationProvider
import com.example.weather_forecast.utils.getApiLangCode
import com.example.weather_forecast.utils.getCenterLocation
import com.example.weather_forecast.utils.getTopBarLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WeatherViewModel(private val locationProvider: LocationProvider , private  val weatherRepository: WeatherRepository , private val app : Application) : ViewModel() {

    private val _locationState = MutableStateFlow<Location>(Location(""))
    val locationState: StateFlow<Location>
        get() = _locationState
    private val _openLocationSettings = MutableSharedFlow<Unit>()
    val openLocationSettings: SharedFlow<Unit>
        get() = _openLocationSettings
    private val _weatherUiState = MutableStateFlow<UiState<WeatherState>>(UiState.Idle)
    val weatherUiState: StateFlow<UiState<WeatherState>>
        get() = _weatherUiState
    private val _locationSource = MutableStateFlow(weatherRepository.getLocationSource())
    val locationSource: StateFlow<LocationSource>
        get() = _locationSource


    fun checkLocationAndFetch() {
        when (_locationSource.value) {
            LocationSource.GPS -> fetchWithGps()
            LocationSource.MAP -> {
                val saved = weatherRepository.getManualLocation()
                if (saved != null) {
                    fetchWeather(saved.first, saved.second)
                } else {
                    fetchWithGps()
                }
            }
        }
    }

    private fun fetchWithGps() {
        if (locationProvider.isLocationEnabled()) {
            locationProvider.getFreshLocation { location ->
                _locationState.value = location
                fetchWeather(location.latitude, location.longitude)
            }
        } else {
            viewModelScope.launch { _openLocationSettings.emit(Unit) }
        }
    }


    private fun fetchWeather(
        lat: Double,
        lon: Double,
        apiKey: String = "3ec08632a7a945e6408e9414cd1fab66"
    ) {
        val lang = weatherRepository.getLanguage()
        viewModelScope.launch {

            flow {
                emit(weatherRepository.getOneCallResponse(lat, lon, apiKey, lang))
            }
                .onStart { _weatherUiState.value = UiState.Loading }
                .catch { ex -> _weatherUiState.value = UiState.Error(ex.message ?: "Unknown error") }
                .collect { response ->
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            _weatherUiState.value = UiState.Loading
                            val topBar = withContext(Dispatchers.IO) { getTopBarLocation(app, lat, lon) }
                            val center = withContext(Dispatchers.IO) { getCenterLocation(app, lat, lon) }
                            _weatherUiState.value = UiState.Success(
                                WeatherState(
                                    oneCall        = body,
                                    topBarLocation = topBar,
                                    centerLocation = center
                                )
                            )
                        } else {
                            _weatherUiState.value = UiState.Error("Empty response")
                        }
                    } else {
                        _weatherUiState.value = UiState.Error("Error ${response.code()}: ${response.message()}")
                    }
                }
        }
    }
    fun updateLocationSource(src: LocationSource) {
        _locationSource.value = src
    }
}


class WeatherViewModelFactory(private val locationProvider: LocationProvider ,private  val weatherRepository: WeatherRepository, private  val app : Application) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WeatherViewModel(locationProvider , weatherRepository, app) as T
    }
}

