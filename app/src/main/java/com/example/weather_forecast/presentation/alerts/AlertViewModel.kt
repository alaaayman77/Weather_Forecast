package com.example.weather_forecast.presentation.alerts

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.AlertTab
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.presentation.weather.AlertState
import com.example.weather_forecast.presentation.weather.FavouriteState
import com.example.weather_forecast.presentation.weather.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlertViewModel(
    private val weatherRepository: WeatherRepository,
    private val app: Application
) : ViewModel() {

    private val apiKey    = "3ec08632a7a945e6408e9414cd1fab66"
    private val scheduler = AlertScheduler(app)

    private val _weatherAlertsState = MutableStateFlow<UiState<AlertState>>(UiState.Idle)
    val weatherAlertsState: StateFlow<UiState<AlertState>> = _weatherAlertsState.asStateFlow()

    private val _scheduledAlerts = MutableStateFlow<List<AlertEntity>>(emptyList())
    val scheduledAlerts: StateFlow<List<AlertEntity>> = _scheduledAlerts.asStateFlow()



    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _showPermDialog = MutableStateFlow(false)
    val showPermDialog: StateFlow<Boolean> = _showPermDialog.asStateFlow()

    private val _alertStatuses = MutableStateFlow<Map<Int, AlertStatus>>(emptyMap())
    val alertStatuses: StateFlow<Map<Int, AlertStatus>> = _alertStatuses.asStateFlow()
init{
    loadScheduledAlerts()
}


    fun onFabClicked() {
        if (!canScheduleExactAlarms()) _showPermDialog.value = true
        else _showBottomSheet.value = true
    }

    fun onDismissBottomSheet() { _showBottomSheet.value = false }
    fun onDismissPermDialog()  { _showPermDialog.value  = false }


    fun updateAlertStatus(alertId: Int, status: AlertStatus) {
        viewModelScope.launch {
                weatherRepository.updateStatus(alertId, status)
        }}

    fun fetchWeatherAlerts(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherAlertsState.value = UiState.Loading
            weatherRepository.getWeatherAlerts(lat, lon, apiKey)
                .onSuccess { alerts ->
                    Log.d("AlertDebug", "Fetched ${alerts.size} alerts: ${alerts.map { it.event }}")
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
            weatherRepository.getAllAlerts()
                .collect { items ->
                    _scheduledAlerts.value = items
                }
        }

    }

    fun scheduleAlert(
        type       : AlertType,
        startMillis: Long,
        endMillis  : Long,
        startLabel : String,
        endLabel   : String
    ) {
        val id           = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        val activeAlerts = (_weatherAlertsState.value as? UiState.Success)?.data?.alerts ?: emptyList()

        val label = activeAlerts.firstOrNull { alert ->
            startMillis < alert.end * 1000L && endMillis > alert.start * 1000L
        }?.event ?: "Weather looks good! No active warnings for your selected time."

        val item = AlertEntity(
            id          = id,
            type        = type,
            startMillis = startMillis,
            endMillis   = endMillis,
            label       = label,
            status      = AlertStatus.SCHEDULED
        )

        scheduler.schedule(id, type, label, startMillis)
        scheduler.scheduleDismiss(id, endMillis)

        viewModelScope.launch {
            weatherRepository.insertAlert(item)
        }
    }


    fun cancelAlert(item: AlertEntity) {

        viewModelScope.launch {
            weatherRepository.deleteAlert(item.id)
        }
    }

    fun canScheduleExactAlarms(): Boolean = scheduler.canScheduleExactAlarms()
}

class AlertViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        AlertViewModel(weatherRepository, app) as T
}