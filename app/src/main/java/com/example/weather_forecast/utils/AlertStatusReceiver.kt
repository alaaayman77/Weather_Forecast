package com.example.weather_forecast.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.presentation.alerts.AlertReceiver

class AlertStatusReceiver(
    private val onStatusUpdate: (alertId: Int, status: AlertStatus) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != AlertReceiver.ACTION_STATUS_UPDATE) return
        val alertId = intent.getIntExtra(AlertReceiver.EXTRA_ALERT_ID, -1)
        val status  = try {
            AlertStatus.valueOf(
                intent.getStringExtra(AlertReceiver.EXTRA_STATUS) ?: return
            )
        } catch (e: Exception) {
            Log.e(TAG, "Unknown status: ${e.message}")
            return
        }

        onStatusUpdate(alertId, status)
    }

    companion object {
        private const val TAG = "AlertStatusReceiver"
    }
}