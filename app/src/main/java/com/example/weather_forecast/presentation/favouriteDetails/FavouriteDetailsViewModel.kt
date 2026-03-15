package com.example.weather_forecast.presentation.favouriteDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.presentation.UiState
import com.example.weather_forecast.presentation.WeatherState
import com.example.weather_forecast.utils.getCenterLocation
import com.example.weather_forecast.utils.getTopBarLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
        val lang = weatherRepository.getLanguage()
        viewModelScope.launch {

            flow {
                emit(weatherRepository.getOneCallResponse(lat, lon, apiKey, lang))
            }
                .onStart { _uiState.value = UiState.Loading }
                .catch { ex -> _uiState.value = UiState.Error(ex.message ?: "Unknown error") }
                .collect { response ->
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            _uiState.value = UiState.Loading
                            val topBar = withContext(Dispatchers.IO) { getTopBarLocation(app, lat, lon) }
                            val center = withContext(Dispatchers.IO) { getCenterLocation(app, lat, lon) }
                            _uiState.value = UiState.Success(
                                WeatherState(
                                    oneCall        = body,
                                    topBarLocation = topBar,
                                    centerLocation = center
                                )
                            )
                        } else {
                            _uiState.value = UiState.Error("Empty response")
                        }
                    } else {
                        _uiState.value = UiState.Error("Error ${response.code()}: ${response.message()}")
                    }
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