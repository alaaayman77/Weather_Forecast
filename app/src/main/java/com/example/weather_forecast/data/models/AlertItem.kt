package com.example.weather_forecast.data.models



enum class AlertStatus { SCHEDULED, ACTIVE, DISMISSED, CANCELLED }
data class AlertItem(
    val id         : Int,
    val type       : AlertType,
    val startMillis: Long,
    val endMillis  : Long,
    val label      : String,
    val status     : AlertStatus = AlertStatus.SCHEDULED,
    val isActive   : Boolean = true
)