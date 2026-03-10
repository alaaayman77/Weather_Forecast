package com.example.weather_forecast.presentation.alerts

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.weather_forecast.data.models.AlertType
import java.util.Calendar

class AlertScheduler(context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val appContext   = context.applicationContext
    //to start alarm
    fun schedule(id: Int, type: AlertType, label: String, triggerAtMillis: Long) {
        val pendingIntent  = buildStartPendingIntent(id, type, label)
//        val now = System.currentTimeMillis()
        //seconds before alarm to view logs
//        val diffSec = (triggerAtMillis - now) / 1000
//
//        Log.d(TAG, " schedule() id=$id type=$type label=$label")
//        Log.d(TAG, " triggerAtMillis=$triggerAtMillis  now=$now  diff=${diffSec}s")
//
//        if (triggerAtMillis <= now) {
//            Log.w(TAG, "triggerAtMillis is in the past! Alarm will NOT fire.")
//            return
//        }

        scheduleExact(triggerAtMillis, pendingIntent)
//        Log.d(TAG, "Alarm scheduled — fires in ${diffSec}s")
    }
    //to dismiss alarm at end time
    fun scheduleDismiss(id: Int, triggerAtMillis: Long) {
        Log.d(TAG, "scheduleDismiss() id=$id")
        scheduleExact(triggerAtMillis, buildDismissPendingIntent(id))
    }
    //cancel both start time and end time alarms
    fun cancel(id: Int, type: AlertType, label: String) {
        Log.d(TAG, "cancel() id=$id")
        alarmManager.cancel(buildStartPendingIntent(id, type, label))
        alarmManager.cancel(buildDismissPendingIntent(id))
    }


    fun canScheduleExactAlarms(): Boolean {
        val result = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> true
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S        -> alarmManager.canScheduleExactAlarms()
            else                                                   -> true
        }
        Log.d(TAG, "canScheduleExactAlarms=$result  SDK=${Build.VERSION.SDK_INT}")
        return result
    }

    //determine type of alarm based on android versions
    private fun scheduleExact(triggerAtMillis: Long, pi: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
        }
    }

    private fun buildStartPendingIntent(id: Int, type: AlertType, label: String): PendingIntent {
        val intent = Intent(appContext, AlertReceiver::class.java).apply {
            putExtra(AlertReceiver.EXTRA_ALERT_ID,   id)
            putExtra(AlertReceiver.EXTRA_ALERT_TYPE, type.name)
            putExtra(AlertReceiver.EXTRA_LABEL,      label)
        }
        //trigger broadcast
        return PendingIntent.getBroadcast(
            appContext, id, intent,
            //update extras if pending intent exist     prevents from changing intent from other apps
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildDismissPendingIntent(id: Int): PendingIntent {
        val intent = Intent(appContext, AlertNotificationService::class.java).apply {
            action = AlertReceiver.ACTION_DISMISS
            putExtra(AlertReceiver.EXTRA_ALERT_ID, id)
        }
        return PendingIntent.getService(
            appContext, id + 100_000, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val TAG = "AlertScheduler"

        fun toEpochMillis(hour24: Int, minute: Int): Long {
            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour24)
                set(Calendar.MINUTE,      minute)
                set(Calendar.SECOND,      0)
                set(Calendar.MILLISECOND, 0)
                if (timeInMillis < System.currentTimeMillis())
                    add(Calendar.DAY_OF_YEAR, 1)
            }
            Log.d(TAG, "toEpochMillis($hour24:${minute.toString().padStart(2,'0')}) → ${cal.timeInMillis}")
            return cal.timeInMillis
        }
    }
}