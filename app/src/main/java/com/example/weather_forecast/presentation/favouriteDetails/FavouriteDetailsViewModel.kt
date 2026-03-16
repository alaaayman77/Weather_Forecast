package com.example.weather_forecast.presentation.favouriteDetails

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.BuildConfig
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.OneCallResponse
import com.example.weather_forecast.presentation.UiState
import com.example.weather_forecast.presentation.WeatherState
import com.example.weather_forecast.utils.getCenterLocation
import com.example.weather_forecast.utils.getTopBarLocation
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class FavouriteDetailsViewModel(
    private val weatherRepository: WeatherRepository,
    private val app: Application
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<WeatherState>>(UiState.Idle)
    val uiState: StateFlow<UiState<WeatherState>> get() = _uiState

    private val _isOffline = MutableStateFlow(false)
    val isOffline: StateFlow<Boolean> get() = _isOffline

    private val gson = Gson()

    fun fetchWeather(lat: Double, lon: Double, apiKey: String = BuildConfig.OPEN_WEATHER_API_KEY) {
        val lang = weatherRepository.getLanguage()
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            try {
                val response = weatherRepository.getOneCallResponse(lat, lon, apiKey, lang)
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        weatherRepository.saveCurrentWeather(gson.toJson(body))
                        _isOffline.value = false

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
                        tryLoadCache(lat, lon, "Empty response")
                    }
                } else {
                    tryLoadCache(lat, lon, "Error ${response.code()}: ${response.message()}")
                }
            } catch (e: IOException) {

                tryLoadCache(lat, lon, e.message ?: "No internet connection")
            } catch (e: Exception) {
                tryLoadCache(lat, lon, e.message ?: "Unknown error")
            }
        }
    }

    private suspend fun tryLoadCache(lat: Double, lon: Double, error: String) {
        val cachedJson = weatherRepository.getCurrentWeather()
        if (cachedJson != null) {
            val cached = gson.fromJson(cachedJson, OneCallResponse::class.java)
            val topBar = withContext(Dispatchers.IO) { getTopBarLocation(app, lat, lon) }
            val center = withContext(Dispatchers.IO) { getCenterLocation(app, lat, lon) }
            _isOffline.value      = true
            _uiState.value = UiState.Success(
                WeatherState(
                    oneCall        = cached,
                    topBarLocation = topBar,
                    centerLocation = center
                )
            )
        } else {
            _isOffline.value      = false
            _uiState.value = UiState.Error(error)
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