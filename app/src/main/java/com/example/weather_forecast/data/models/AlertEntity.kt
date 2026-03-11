package com.example.weather_forecast.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


enum class AlertStatus { SCHEDULED, ACTIVE, DISMISSED, CANCELLED }
@Entity(tableName = "alerts")
data class AlertEntity(
    @PrimaryKey
    val id         : Int,
    val type       : AlertType,
    val startMillis: Long,
    val endMillis  : Long,
    val label      : String,
    val status     : AlertStatus = AlertStatus.SCHEDULED,
    val isActive   : Boolean = true
)