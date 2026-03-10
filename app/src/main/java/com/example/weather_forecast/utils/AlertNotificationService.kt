package com.example.weather_forecast.presentation.alerts

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.example.weather_forecast.MainActivity
import com.example.weather_forecast.R

class AlertNotificationService : Service() {

    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator?       = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service onCreate")
        createNotificationChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand — action=${intent?.action}")

        if (intent?.action == AlertReceiver.ACTION_DISMISS) {
            Log.d(TAG, "Dismiss received — stopping")
            //stops sound and vibrator
            stopAlarm()
            //remove notification
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            //destroy service
            stopSelf()
            return START_NOT_STICKY
        }

        val alertId   = intent?.getIntExtra(AlertReceiver.EXTRA_ALERT_ID, startId) ?: startId
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
            AlertType.ALERT        -> startAlertMode(alertId, label)
            AlertType.NOTIFICATION -> startNotificationMode(alertId, label)
        }
        return START_NOT_STICKY
    }

    private fun startAlertMode(alertId: Int, label: String) {
        Log.d(TAG, "startAlertMode id=$alertId")

        val dismissIntent    = dismissPendingIntent(alertId)
        val fullScreenIntent = PendingIntent.getActivity(
            this, alertId,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ALERT)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Weather Alert")
            .setContentText(label)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenIntent, true)
            .addAction(0, "Dismiss", dismissIntent)
            .addAction(0, "Cancel",  dismissIntent)
            .build()

        startForegroundCompat(alertId, notification)

        try { playRingtone();
            Log.d(TAG, "Ringtone started")
        }
        catch (e: Exception) {
            Log.e(TAG, "playRingtone: ${e.message}", e)
        }

        try {
            vibrate(); Log.d(TAG, "Vibration started")
        }
        catch (e: Exception) { Log.e(TAG, "vibrate: ${e.message}", e) }
    }



    private fun startNotificationMode(alertId: Int, label: String) {
        Log.d(TAG, "startNotificationMode id=$alertId")

        val notification = NotificationCompat.Builder(this, CHANNEL_NOTIFICATION)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Weather Notification")
            .setContentText(label)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setSilent(true)
            .addAction(0, "Dismiss", dismissPendingIntent(alertId))
            .build()

        startForegroundCompat(alertId, notification)

        // STOP_FOREGROUND_DETACH keeps the notification alive after service stops
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_DETACH)
        stopSelf()
    }

    // ── startForeground with type on Android 14+ ──────────────────────────────

    private fun startForegroundCompat(id: Int, notification: Notification) {
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
        val pattern = longArrayOf(0, 500, 500, 500, 500)
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

    private fun dismissPendingIntent(alertId: Int): PendingIntent {
        val intent = Intent(this, AlertNotificationService::class.java).apply {
            action = AlertReceiver.ACTION_DISMISS
            putExtra(AlertReceiver.EXTRA_ALERT_ID, alertId)
        }
        return PendingIntent.getService(
            this, alertId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }



    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (nm.getNotificationChannel(CHANNEL_ALERT) == null) {
            NotificationChannel(CHANNEL_ALERT, "Weather Alerts", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Full-screen alarm notifications with sound"
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM),
                    AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).build()
                )
                enableVibration(true)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                nm.createNotificationChannel(this)
            }
            Log.d(TAG, "Created CHANNEL_ALERT")
        }

        if (nm.getNotificationChannel(CHANNEL_NOTIFICATION) == null) {
            NotificationChannel(CHANNEL_NOTIFICATION, "Weather Notifications", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Silent weather update notifications"
                setSound(null, null)
                enableVibration(false)
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                nm.createNotificationChannel(this)
            }
            Log.d(TAG, "✅ Created CHANNEL_NOTIFICATION")
        }
    }

    override fun onDestroy() {
        Log.d(TAG, "Service onDestroy")
        stopAlarm()
        super.onDestroy()
    }

    companion object {
        private const val TAG= "AlertNotifService"
        const val CHANNEL_ALERT = "channel_weather_alert"
        const val CHANNEL_NOTIFICATION= "channel_weather_notification"
    }
}