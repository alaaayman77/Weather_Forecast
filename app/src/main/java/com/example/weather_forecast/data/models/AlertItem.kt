package com.example.weather_forecast.data.models

import com.example.weather_forecast.presentation.alerts.AlertType

data class AlertItem(
    val id         : Int,
    val type       : AlertType,
    val startMillis: Long,
    val endMillis  : Long,
    val label      : String,
    val isActive   : Boolean = true
)