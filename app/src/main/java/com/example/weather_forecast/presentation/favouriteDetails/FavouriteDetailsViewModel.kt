package com.example.weather_forecast.presentation.favouriteDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.presentation.weather.UiState
import com.example.weather_forecast.presentation.weather.WeatherState
import com.example.weather_forecast.utils.getCenterLocation
import com.example.weather_forecast.utils.getTopBarLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteDetailsViewModel(
    private val weatherRepository: WeatherRepository,
    private val app: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<WeatherState>>(UiState.Idle)
    val uiState: StateFlow<UiState<WeatherState>> get() = _uiState

    fun fetchWeather(
        lat: Double,
        lon: Double,
        apiKey: String = "3ec08632a7a945e6408e9414cd1fab66"
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = weatherRepository.getOneCallResponse(lat, lon, apiKey)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        _uiState.value = UiState.Success(WeatherState(oneCall = body))
                        launch(Dispatchers.IO) {
                            val topBar  = getTopBarLocation(app, lat, lon)
                            val center  = getCenterLocation(app, lat, lon)
                            val current = _uiState.value
                            if (current is UiState.Success) {
                                _uiState.value = UiState.Success(
                                    current.data.copy(
                                        topBarLocation = topBar,
                                        centerLocation = center
                                    )
                                )
                            }
                        }
                    } else {
                        _uiState.value = UiState.Error("Empty response")
                    }
                } else {
                    _uiState.value = UiState.Error("Error ${response.code()}: ${response.message()}")
                }
            } catch (ex: Exception) {
                _uiState.value = UiState.Error(ex.message ?: "Unknown error")
            }
        }
    }
}

class FavouriteDetailsViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FavouriteDetailsViewModel(weatherRepository, app) as T
    }
}