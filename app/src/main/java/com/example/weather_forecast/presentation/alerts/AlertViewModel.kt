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
import com.example.weather_forecast.presentation.weather.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _selectedTab = MutableStateFlow(AlertTab.ACTIVE)
    val selectedTab: StateFlow<AlertTab> = _selectedTab.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _showPermDialog = MutableStateFlow(false)
    val showPermDialog: StateFlow<Boolean> = _showPermDialog.asStateFlow()

    private val _alertStatuses = MutableStateFlow<Map<Int, AlertStatus>>(emptyMap())
    val alertStatuses: StateFlow<Map<Int, AlertStatus>> = _alertStatuses.asStateFlow()

    fun onTabSelected(tab: AlertTab) { _selectedTab.value = tab }

    fun onFabClicked() {
        if (!canScheduleExactAlarms()) _showPermDialog.value = true
        else _showBottomSheet.value = true
    }

    fun onDismissBottomSheet() { _showBottomSheet.value = false }
    fun onDismissPermDialog()  { _showPermDialog.value  = false }

    fun updateAlertStatus(alertId: Int, status: AlertStatus) {
        val newMap = _alertStatuses.value.toMutableMap()
        newMap[alertId] = status
        _alertStatuses.value = newMap
    }

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


    fun scheduleAlert(
        type       : AlertType,
        startMillis: Long,
        endMillis  : Long,
        startLabel : String,
        endLabel   : String
    ) {
        val id           = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        val activeAlerts = (_weatherAlertsState.value as? UiState.Success)?.data?.alerts ?: emptyList()

        val overlappingAlert = activeAlerts.firstOrNull { networkAlert ->
            val alertStart = networkAlert.start * 1000L
            val alertEnd   = networkAlert.end   * 1000L
            startMillis < alertEnd && endMillis > alertStart
        }

        val label = overlappingAlert?.event
            ?: "Weather looks good! No active warnings for your selected time."

        scheduler.schedule(id, type, label, startMillis)
        scheduler.scheduleDismiss(id, endMillis)

        _scheduledAlerts.update { list ->
            list + AlertEntity(
                id          = id,
                type        = type,
                startMillis = startMillis,
                endMillis   = endMillis,
                label       = label
            )
        }
        _alertStatuses.update { it + (id to AlertStatus.SCHEDULED) }
    }

    fun cancelAlert(item: AlertEntity) {

        val newList = mutableListOf<AlertEntity>()
        for (alert in _scheduledAlerts.value) {
            if (alert.id != item.id) {
                newList.add(alert)
            }
        }
        _scheduledAlerts.value = newList

        val newMap = mutableMapOf<Int, AlertStatus>()
        for ((id, status) in _alertStatuses.value) {
            if (id != item.id) {
                newMap[id] = status
            }
        }
        _alertStatuses.value = newMap
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