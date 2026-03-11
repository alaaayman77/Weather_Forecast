package com.example.weather_forecast.utils

import androidx.room.TypeConverter
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.AlertType

class AlertsTypeConvertors {
    @TypeConverter fun fromAlertType(type: AlertType): String= type.name
    @TypeConverter fun toAlertType(name: String)     : AlertType = AlertType.valueOf(name)
    @TypeConverter fun fromAlertStatus(status: AlertStatus): String  = status.name
    @TypeConverter fun toAlertStatus(name: String): AlertStatus = AlertStatus.valueOf(name)
}