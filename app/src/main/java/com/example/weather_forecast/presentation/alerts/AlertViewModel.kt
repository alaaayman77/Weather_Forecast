package com.example.weather_forecast.presentation.alerts

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.models.AlertItem
import com.example.weather_forecast.data.models.AlertTab
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.data.models.WeatherAlert
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
    val weatherAlertsState: StateFlow<UiState<AlertState>> =
        _weatherAlertsState.asStateFlow()

    private val _scheduledAlerts = MutableStateFlow<List<AlertItem>>(emptyList())
    val scheduledAlerts: StateFlow<List<AlertItem>> = _scheduledAlerts.asStateFlow()

    private val _selectedTab = MutableStateFlow(AlertTab.ACTIVE)
    val selectedTab: StateFlow<AlertTab> = _selectedTab.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _showPermDialog = MutableStateFlow(false)
    val showPermDialog: StateFlow<Boolean> = _showPermDialog.asStateFlow()



    fun onTabSelected(tab: AlertTab) {
        _selectedTab.value = tab
    }

    fun onFabClicked() {
        if (!canScheduleExactAlarms()) _showPermDialog.value = true
        else _showBottomSheet.value = true
    }

    fun onDismissBottomSheet() {
        _showBottomSheet.value = false
    }

    fun onDismissPermDialog() {
        _showPermDialog.value = false
    }


    fun fetchWeatherAlerts(lat: Double, lon: Double) {
        viewModelScope.launch {
            _weatherAlertsState.value = UiState.Loading
            weatherRepository.getWeatherAlerts(lat, lon, apiKey)
                .onSuccess { alerts ->
                    Log.d("AlertDebug", "Fetched ${alerts.size} alerts: ${alerts.map { it.event }}")
                    _weatherAlertsState.value = UiState.Success(AlertState(alerts = alerts))
                    alerts.forEach { autoSchedule(it) }
                }
                .onFailure { e ->
                    _weatherAlertsState.value =
                        UiState.Error(e.message ?: "Failed to fetch weather alerts")
                }
        }
    }

    private fun autoSchedule(owmAlert: WeatherAlert) {
        val startMillis = owmAlert.start * 1000L
        val endMillis   = owmAlert.end   * 1000L
        val now         = System.currentTimeMillis()

        if (startMillis < now) return
        if (_scheduledAlerts.value.any { it.label == owmAlert.event }) return

        val id = Math.abs(owmAlert.event.hashCode())
        scheduler.schedule(id, AlertType.ALERT, owmAlert.event, startMillis)
        scheduler.scheduleDismiss(id, endMillis)

        _scheduledAlerts.update { list ->
            list + AlertItem(
                id          = id,
                type        = AlertType.ALERT,
                startMillis = startMillis,
                endMillis   = endMillis,
                label       = owmAlert.event
            )
        }
    }

    fun scheduleAlert(
        type       : AlertType,
        startMillis: Long,
        endMillis  : Long,
        startLabel : String,
        endLabel   : String
    ) {
        val id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()


        val activeAlerts = (_weatherAlertsState.value as? UiState.Success)?.data?.alerts ?: emptyList()

        val overlappingAlert = activeAlerts.firstOrNull { networkAlert ->
            val alertStart = networkAlert.start * 1000L
            val alertEnd   = networkAlert.end   * 1000L
            startMillis < alertEnd && endMillis > alertStart
        }

        val label = when {
            overlappingAlert != null -> overlappingAlert.event
            else -> "Weather looks good! No active warnings for your selected time."
        }

        scheduler.schedule(id, type, label, startMillis)
        scheduler.scheduleDismiss(id, endMillis)

        _scheduledAlerts.update { list ->
            list + AlertItem(
                id          = id,
                type        = type,
                startMillis = startMillis,
                endMillis   = endMillis,
                label       = label
            )
        }
    }

    fun cancelAlert(item: AlertItem) {
        scheduler.cancel(item.id, item.type, item.label)
        _scheduledAlerts.update { list -> list.filter { it.id != item.id } }
    }

    fun canScheduleExactAlarms(): Boolean = scheduler.canScheduleExactAlarms()
}


class AlertViewModelFactory(private val weatherRepository: WeatherRepository, private val app: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(weatherRepository, app) as T
    }
}
