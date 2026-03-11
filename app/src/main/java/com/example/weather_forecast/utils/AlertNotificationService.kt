package com.example.weather_forecast.presentation.alerts

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.weather_forecast.MainActivity
import com.example.weather_forecast.R
import com.example.weather_forecast.data.models.AlertType

class AlertNotificationService : Service() {

    private var mediaPlayer: MediaPlayer?  = null
    private var vibrator: Vibrator? = null
    private var currentAlertId  : Int = -1
    private var currentNotification: Notification? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand — action=${intent?.action}")

        when (intent?.action) {

            AlertReceiver.ACTION_DISMISS -> {
                val alertId   = intent.getIntExtra(AlertReceiver.EXTRA_ALERT_ID, -1)
                val alertType = intent.getStringExtra(AlertReceiver.EXTRA_ALERT_TYPE)
                    ?: AlertType.NOTIFICATION.name
                val label     = intent.getStringExtra(AlertReceiver.EXTRA_LABEL) ?: "Weather Alert"

                stopAlarm()
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)

                // reschedule same alert for tomorrow at the same time
                val tomorrowMillis = System.currentTimeMillis() + 24 * 60 * 60 * 1000L
                AlertScheduler(this).schedule(
                    id              = alertId,
                    type            = AlertType.valueOf(alertType),
                    label           = label,
                    triggerAtMillis = tomorrowMillis
                )
                Log.d(TAG, "Dismissed — rescheduled for tomorrow id=$alertId")

                stopSelf()
                return START_NOT_STICKY
            }

            AlertReceiver.ACTION_CANCEL -> {
                val alertId   = intent.getIntExtra(AlertReceiver.EXTRA_ALERT_ID, -1)
                val alertType = intent.getStringExtra(AlertReceiver.EXTRA_ALERT_TYPE)
                    ?: AlertType.NOTIFICATION.name
                val label     = intent.getStringExtra(AlertReceiver.EXTRA_LABEL) ?: "Weather Alert"

                stopAlarm()
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)

                AlertScheduler(this).cancel(
                    id    = alertId,
                    type  = AlertType.valueOf(alertType),
                    label = label
                )
                Log.d(TAG, "Cancelled entirely id=$alertId")

                stopSelf()
                return START_NOT_STICKY
            }
        }


        val alertId = intent?.getIntExtra(AlertReceiver.EXTRA_ALERT_ID, startId) ?: startId
        val alertType = try {
            AlertType.valueOf(
                intent?.getStringExtra(AlertReceiver.EXTRA_ALERT_TYPE) ?: AlertType.NOTIFICATION.name
            )
        } catch (e: Exception) {
            AlertType.NOTIFICATION
        }
        val label = intent?.getStringExtra(AlertReceiver.EXTRA_LABEL) ?: "Weather Alert"
        Log.d(TAG, "   alertId=$alertId  type=$alertType  label=$label")

        when (alertType) {
            AlertType.ALERT        -> startAlertMode(alertId, alertType.name, label)
            AlertType.NOTIFICATION -> startNotificationMode(alertId, alertType.name, label)
        }


        return START_STICKY
    }



    private fun startAlertMode(alertId: Int, alertType: String, label: String) {
        val dismissIntent    = dismissPendingIntent(alertId, alertType, label)
        val cancelIntent     = cancelPendingIntent(alertId, alertType, label)
        val fullScreenIntent = PendingIntent.getActivity(
            this, alertId,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val largeIconBitmap = android.graphics.Bitmap.createBitmap(
            96, 96, android.graphics.Bitmap.Config.ARGB_8888
        )
        android.graphics.Canvas(largeIconBitmap).also { canvas ->
            val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)

            paint.color = 0xFF1565C0.toInt()
            canvas.drawCircle(48f, 48f, 48f, paint)

            paint.color = 0xFF1E88E5.toInt()
            paint.style = android.graphics.Paint.Style.STROKE
            paint.strokeWidth = 4f
            canvas.drawCircle(48f, 48f, 38f, paint)

            paint.style     = android.graphics.Paint.Style.FILL
            paint.color     = android.graphics.Color.WHITE
            paint.textSize  = 58f
            paint.textAlign = android.graphics.Paint.Align.CENTER
            paint.typeface  = android.graphics.Typeface.DEFAULT_BOLD
            canvas.drawText("!", 48f, 70f, paint)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ALERT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(largeIconBitmap)
            .setContentTitle("⚠️ Weather Alert Active")
            .setContentText(label)
            .setSubText("Tap for details")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(label)
                    .setBigContentTitle("⚠️ Weather Alert Active")
                    .setSummaryText("Active alert for your area")
            )
            .setColor(0xFF1E88E5.toInt())
            .setColorized(true)
            .setSortKey("0")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setOnlyAlertOnce(false)
            .setFullScreenIntent(fullScreenIntent, true)
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_launcher_foreground,
                    "✓  Dismiss",
                    dismissIntent
                ).build()
            )
            .addAction(
                NotificationCompat.Action.Builder(
                    R.drawable.ic_launcher_foreground,
                    "✕  Cancel Alert",
                    cancelIntent
                ).build()
            )
            .build()
            .also {
                it.flags = it.flags or
                        Notification.FLAG_NO_CLEAR or
                        Notification.FLAG_ONGOING_EVENT
            }

        startForegroundCompat(alertId, notification)
        try { playRingtone() } catch (e: Exception) { Log.e(TAG, "playRingtone: ${e.message}", e) }
        try { vibrate()      } catch (e: Exception) { Log.e(TAG, "vibrate: ${e.message}", e)      }
    }




    private fun startNotificationMode(alertId: Int, alertType: String, label: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_NOTIFICATION)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("🌤️ Weather Update")
            .setContentText(label)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(label)
                    .setBigContentTitle("🌤️ Weather Update")
                    .setSummaryText("Scheduled weather notification")
            )
            .setColor(0xFF1E88E5.toInt())
            .setColorized(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSilent(true)
            .addAction(0, "✓  Dismiss",      dismissPendingIntent(alertId, alertType, label))
            .addAction(0, "✕  Cancel", cancelPendingIntent(alertId, alertType, label))
            .build()

        startForegroundCompat(alertId, notification)

    }



    private fun startForegroundCompat(id: Int, notification: Notification) {
        currentAlertId      = id
        currentNotification = notification
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                startForeground(id, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
            } else {
                startForeground(id, notification)
            }
            Log.d(TAG, "startForeground called id=$id")
        } catch (e: Exception) {
            Log.e(TAG, "startForeground failed: ${e.message}", e)
        }
    }

    // Re-post notification if app is removed from recents
    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val notification = currentNotification ?: return
        if (currentAlertId == -1) return
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(currentAlertId, notification)
        Log.d(TAG, "onTaskRemoved — re-posted notification id=$currentAlertId")
    }



    private fun playRingtone() {
        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        Log.d(TAG, "   Ringtone URI: $uri")
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(applicationContext, uri)
            isLooping = true
            prepare()
            start()
        }
    }

    private fun vibrate() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 400, 200, 400)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopAlarm() {
        mediaPlayer?.apply { if (isPlaying) stop(); release() }
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
    }

    private fun dismissPendingIntent(alertId: Int, alertType: String, label: String): PendingIntent {
        val intent = Intent(this, AlertNotificationService::class.java).apply {
            action = AlertReceiver.ACTION_DISMISS
            putExtra(AlertReceiver.EXTRA_ALERT_ID,   alertId)
            putExtra(AlertReceiver.EXTRA_ALERT_TYPE, alertType)
            putExtra(AlertReceiver.EXTRA_LABEL,      label)
        }
        return PendingIntent.getService(
            this, alertId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun cancelPendingIntent(alertId: Int, alertType: String, label: String): PendingIntent {
        val intent = Intent(this, AlertNotificationService::class.java).apply {
            action = AlertReceiver.ACTION_CANCEL
            putExtra(AlertReceiver.EXTRA_ALERT_ID,   alertId)
            putExtra(AlertReceiver.EXTRA_ALERT_TYPE, alertType)
            putExtra(AlertReceiver.EXTRA_LABEL,      label)
        }
        return PendingIntent.getService(
            this, alertId + 200_000, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }



    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        nm.deleteNotificationChannel("channel_weather_alert")
        nm.deleteNotificationChannel("channel_weather_notification")

        if (nm.getNotificationChannel(CHANNEL_ALERT) == null) {
            NotificationChannel(
                CHANNEL_ALERT,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Full-screen alarm notifications with sound"
                lightColor    = 0xFF5722
                enableLights(true)
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
                )
                enableVibration(true)
                vibrationPattern     = longArrayOf(0, 400, 200, 400)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                nm.createNotificationChannel(this)
            }
            Log.d(TAG, "Created $CHANNEL_ALERT")
        }

        if (nm.getNotificationChannel(CHANNEL_NOTIFICATION) == null) {
            NotificationChannel(
                CHANNEL_NOTIFICATION,
                "Weather Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description          = "Silent weather alert notifications"
                setSound(null, null)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                nm.createNotificationChannel(this)
            }
            Log.d(TAG, "Created $CHANNEL_NOTIFICATION")
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        stopAlarm()
        super.onDestroy()
    }

    companion object {
        private const val TAG  = "AlertNotifService"
        const val CHANNEL_ALERT = "channel_weather_alert_v2"
        const val CHANNEL_NOTIFICATION = "channel_weather_notification_v2"
    }
}