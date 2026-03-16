package com.example.weather_forecast.presentation.alerts

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.BuildConfig
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertMode
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.data.models.CustomCondition
import com.example.weather_forecast.presentation.AlertState
import com.example.weather_forecast.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertViewModel(
    private val weatherRepository: WeatherRepository,
    private val app              : Application
) : ViewModel() {

    private val apiKey    = BuildConfig.OPEN_WEATHER_API_KEY
    private val scheduler = AlertScheduler(app)

    private val _weatherAlertsState = MutableStateFlow<UiState<AlertState>>(UiState.Idle)
    val weatherAlertsState: StateFlow<UiState<AlertState>> = _weatherAlertsState.asStateFlow()

    private val _scheduledAlerts = MutableStateFlow<List<AlertEntity>>(emptyList())
    val scheduledAlerts: StateFlow<List<AlertEntity>> = _scheduledAlerts.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _showPermDialog = MutableStateFlow(false)
    val showPermDialog: StateFlow<Boolean> = _showPermDialog.asStateFlow()

    private val _showNotifPermDialog = MutableStateFlow(false)
    val showNotifPermDialog: StateFlow<Boolean> = _showNotifPermDialog.asStateFlow()

    private val _alertStatuses = MutableStateFlow<Map<Int, AlertStatus>>(emptyMap())
    val alertStatuses: StateFlow<Map<Int, AlertStatus>> = _alertStatuses.asStateFlow()

    init { loadScheduledAlerts() }

    fun onFabClicked(isNotificationGranted: Boolean) {
        when {
            !isNotificationGranted    -> _showNotifPermDialog.value = true
            !canScheduleExactAlarms() -> _showPermDialog.value      = true
            else                      -> _showBottomSheet.value     = true
        }
    }

    fun onDismissNotifPermDialog() { _showNotifPermDialog.value = false }
    fun onDismissBottomSheet()     { _showBottomSheet.value     = false }
    fun onDismissPermDialog()      { _showPermDialog.value      = false }

    fun updateAlertStatus(alertId: Int, status: AlertStatus) {
        viewModelScope.launch {
            weatherRepository.updateStatus(alertId, status)
        }
    }

    fun fetchWeatherAlerts(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherAlertsState.value = UiState.Loading
            weatherRepository.getWeatherAlerts(lat, lon, apiKey)
                .onSuccess { alerts ->
                    Log.d("AlertDebug", "Fetched ${alerts.size} alerts")
                    _weatherAlertsState.value = UiState.Success(AlertState(alerts = alerts))
                }
                .onFailure { e ->
                    _weatherAlertsState.value =
                        UiState.Error(e.message ?: "Failed to fetch weather alerts")
                }
        }
    }

    private fun loadScheduledAlerts() {
        viewModelScope.launch {
            weatherRepository.getAllAlerts().collect { items ->
                _scheduledAlerts.value = items
            }
        }
    }

    fun scheduleAlert(
        type            : AlertType,
        startMillis     : Long,
        endMillis       : Long,
        startLabel      : String,
        endLabel        : String,
        mode            : AlertMode        = AlertMode.SCHEDULED,
        customCondition : CustomCondition? = null
    ) {
        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()

        val label = when (mode) {
            AlertMode.CUSTOM ->
                "Watching: ${customCondition?.emoji} ${customCondition?.label}"
            AlertMode.SCHEDULED -> {
                val activeAlerts =
                    (_weatherAlertsState.value as? UiState.Success)?.data?.alerts ?: emptyList()
                activeAlerts.firstOrNull { alert ->
                    startMillis < alert.end * 1000L && endMillis > alert.start * 1000L
                }?.event ?: "Weather looks good! No active warnings for your selected time."
            }
        }

        val item = AlertEntity(
            id              = id,
            type            = type,
            startMillis     = startMillis,
            endMillis       = endMillis,
            label           = label,
            status          = AlertStatus.SCHEDULED,
            mode            = mode,
            customCondition = customCondition
        )


        if (mode == AlertMode.SCHEDULED) {
            scheduler.schedule(id, type, label, startMillis)
            scheduler.scheduleDismiss(id, endMillis)
        }


        viewModelScope.launch { weatherRepository.insertAlert(item) }
    }

    fun cancelAlert(item: AlertEntity) {
        // Cancel alarm if it's a scheduled alert
        if (item.mode == AlertMode.SCHEDULED) {
            scheduler.cancel(item.id, item.type, item.label)
        }
        viewModelScope.launch { weatherRepository.deleteAlert(item.id) }
    }

    fun canScheduleExactAlarms(): Boolean = scheduler.canScheduleExactAlarms()
}

class AlertViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val app              : Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AlertViewModel(weatherRepository, app) as T
}