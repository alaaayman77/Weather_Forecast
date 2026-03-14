package com.example.weather_forecast.presentation.weather

import android.app.Application
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.AlertMode
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.CustomCondition
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.presentation.alerts.AlertNotificationService
import com.example.weather_forecast.presentation.alerts.AlertReceiver
import com.example.weather_forecast.utils.LocationProvider
import com.example.weather_forecast.utils.getCenterLocation
import com.example.weather_forecast.utils.getTopBarLocation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WeatherViewModel(
    private val locationProvider  : LocationProvider,
    private val weatherRepository : WeatherRepository,
    private val app               : Application
) : ViewModel() {

    private val _locationState = MutableStateFlow<Location>(Location(""))
    val locationState: StateFlow<Location> get() = _locationState

    private val _openLocationSettings = MutableSharedFlow<Unit>()
    val openLocationSettings: SharedFlow<Unit> get() = _openLocationSettings

    private val _weatherUiState = MutableStateFlow<UiState<WeatherState>>(UiState.Idle)
    val weatherUiState: StateFlow<UiState<WeatherState>> get() = _weatherUiState

    private val _locationSource = MutableStateFlow(weatherRepository.getLocationSource())
    val locationSource: StateFlow<LocationSource> get() = _locationSource

    fun checkLocationAndFetch() {
        when (_locationSource.value) {
            LocationSource.GPS -> fetchWithGps()
            LocationSource.MAP -> {
                val saved = weatherRepository.getManualLocation()
                if (saved != null) fetchWeather(saved.first, saved.second)
                else               fetchWithGps()
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
        lat   : Double,
        lon   : Double,
        apiKey: String = "3ec08632a7a945e6408e9414cd1fab66"
    ) {
        val lang = weatherRepository.getLanguage()
        viewModelScope.launch {
            flow {
                emit(weatherRepository.getOneCallResponse(lat, lon, apiKey, lang))
            }
                .onStart { _weatherUiState.value = UiState.Loading }
                .catch  { ex -> _weatherUiState.value = UiState.Error(ex.message ?: "Unknown error") }
                .collect { response ->
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            val topBar = withContext(Dispatchers.IO) { getTopBarLocation(app, lat, lon) }
                            val center = withContext(Dispatchers.IO) { getCenterLocation(app, lat, lon) }

                            _weatherUiState.value = UiState.Success(
                                WeatherState(
                                    oneCall        = body,
                                    topBarLocation = topBar,
                                    centerLocation = center
                                )
                            )

                            // Check custom condition alerts after state is set
                            val owmId       = body.current.weather.firstOrNull()?.id
                            val description = body.current.weather.firstOrNull()?.description ?: ""
                            val tempKelvin  = body.current.temp
                            if (owmId != null) {
                                checkCustomAlerts(owmId, tempKelvin, description)
                            }

                        } else {
                            _weatherUiState.value = UiState.Error("Empty response")
                        }
                    } else {
                        _weatherUiState.value =
                            UiState.Error("Error ${response.code()}: ${response.message()}")
                    }
                }
        }
    }


    private fun checkCustomAlerts(owmId: Int, tempKelvin: Double, description: String) {
        viewModelScope.launch {
            val nowMillis = System.currentTimeMillis()

            weatherRepository.getAllAlerts().first().filter { alert ->
                alert.mode            == AlertMode.CUSTOM      &&
                        alert.status          == AlertStatus.SCHEDULED && // ← ONLY scheduled, never re-fire dismissed/cancelled
                        alert.customCondition != null                  &&
                        nowMillis in alert.startMillis..alert.endMillis
            }.forEach { alert ->
                val conditionMet = alert.customCondition!!.matches(owmId, tempKelvin)
                Log.d("CustomAlert", "alert=${alert.id} condition=${alert.customCondition} met=$conditionMet")

                if (conditionMet) {
                    val tempCelsius = (tempKelvin - 273.15).toInt()
                    val label = when (alert.customCondition) {
                        CustomCondition.HIGH_TEMP ->
                            "🌡️ High temp alert! Currently ${tempCelsius}°C"
                        CustomCondition.LOW_TEMP  ->
                            "🥶 Low temp alert! Currently ${tempCelsius}°C"
                        else ->
                            "${alert.customCondition.emoji} ${description.replaceFirstChar { it.uppercase() }} detected in your area!"
                    }

                    android.content.Intent(app, AlertNotificationService::class.java).apply {
                        putExtra(AlertReceiver.EXTRA_ALERT_ID,   alert.id)
                        putExtra(AlertReceiver.EXTRA_ALERT_TYPE, alert.type.name)
                        putExtra(AlertReceiver.EXTRA_LABEL,      label)
                        putExtra(AlertReceiver.EXTRA_ALERT_MODE, "CUSTOM")
                    }.also {
                        androidx.core.content.ContextCompat.startForegroundService(app, it)
                    }

                    // ── Only set ACTIVE once, don't touch dismissed/cancelled ──
                    weatherRepository.updateStatus(alert.id, AlertStatus.ACTIVE)
                }
            }
        }
    }

    fun updateLocationSource(src: LocationSource) {
        _locationSource.value = src
    }

    fun onLanguageChanged() {
        _weatherUiState.value = UiState.Loading
        checkLocationAndFetch()
    }
}

class WeatherViewModelFactory(
    private val locationProvider : LocationProvider,
    private val weatherRepository: WeatherRepository,
    private val app              : Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        WeatherViewModel(locationProvider, weatherRepository, app) as T
}