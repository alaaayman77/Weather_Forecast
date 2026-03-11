package com.example.weather_forecast.presentation.alerts

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.weather_forecast.data.models.AlertType

class AlertReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive fired — action=${intent.action}")

        val alertId   = intent.getIntExtra(EXTRA_ALERT_ID, -1)
        val alertType = intent.getStringExtra(EXTRA_ALERT_TYPE) ?: AlertType.NOTIFICATION.name
        val label     = intent.getStringExtra(EXTRA_LABEL) ?: "Weather Alert"
        Log.d(TAG, "   alertId=$alertId  type=$alertType  label=$label")

        val serviceIntent = Intent(context, AlertNotificationService::class.java).apply {
            putExtra(EXTRA_ALERT_ID,   alertId)
            putExtra(EXTRA_ALERT_TYPE, alertType)
            putExtra(EXTRA_LABEL,      label)
        }

        try {
            ContextCompat.startForegroundService(context, serviceIntent)
            Log.d(TAG, "startForegroundService called")
        } catch (e: Exception) {
            Log.e(TAG, "startForegroundService failed: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "AlertReceiver"
        const val EXTRA_ALERT_ID   = "extra_alert_id"
        const val EXTRA_ALERT_TYPE = "extra_alert_type"
        const val EXTRA_LABEL      = "extra_label"
        const val ACTION_DISMISS   = "ACTION_DISMISS_ALERT"
        const val ACTION_CANCEL = "ACTION_CANCEL_ALERT"
        const val ACTION_STATUS_UPDATE = "ACTION_STATUS_UPDATE"
        const val EXTRA_STATUS         = "extra_status"
    }
}