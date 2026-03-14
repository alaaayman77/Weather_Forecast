package com.example.weather_forecast.utils

import androidx.room.TypeConverter
import com.example.weather_forecast.data.models.AlertMode
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.data.models.CustomCondition

class AlertsTypeConvertors {
    @TypeConverter fun fromAlertType(v: AlertType)           : String  = v.name
    @TypeConverter fun toAlertType(v: String)                : AlertType = AlertType.valueOf(v)

    @TypeConverter fun fromAlertStatus(v: AlertStatus)       : String  = v.name
    @TypeConverter fun toAlertStatus(v: String)              : AlertStatus = AlertStatus.valueOf(v)

    @TypeConverter fun fromAlertMode(v: AlertMode)           : String  = v.name
    @TypeConverter fun toAlertMode(v: String)                : AlertMode = AlertMode.valueOf(v)

    @TypeConverter fun fromCustomCondition(v: CustomCondition?) : String?  = v?.name
    @TypeConverter fun toCustomCondition(v: String?)            : CustomCondition? =
        v?.let { CustomCondition.valueOf(it) }
}