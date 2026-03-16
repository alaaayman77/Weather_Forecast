package com.example.weather_forecast.presentation.weather.view.components

import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.net.Uri
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.exoplayer.ExoPlayer
import com.example.weather_forecast.R
import java.util.Calendar

@Composable
fun WeatherVideoBackground(weatherId: Int?, dt: Long? , timezone :String) {
    if (weatherId == null) {
        val isNight = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).let { it < 6 || it >= 20 }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        if (isNight) listOf(Color(0xFF0D1B2A), Color(0xFF1B2A4A), Color(0xFF0D1B2A))
                        else         listOf(Color(0xFF90CAF9), Color(0xFFBBDEFB), Color(0xFFE3F2FD))
                    )
                )
        )
        return
    }

    val context  = LocalContext.current
    val videoRes = getWeatherVideoRes(weatherId, dt , timezone)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode    = ExoPlayer.REPEAT_MODE_ALL
            volume        = 0f
            playWhenReady = true
        }
    }

    LaunchedEffect(videoRes) {
        exoPlayer.setMediaItem(
            MediaItem.fromUri(Uri.parse("android.resource://${context.packageName}/$videoRes"))
        )
        exoPlayer.prepare()
        exoPlayer.play()
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            TextureView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
                        exoPlayer.setVideoSurface(Surface(st))
                    }
                    override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) {
                        scaleToFill(this@apply, exoPlayer.videoSize, w, h)
                    }
                    override fun onSurfaceTextureDestroyed(st: SurfaceTexture): Boolean {
                        exoPlayer.setVideoSurface(null)
                        return true
                    }
                    override fun onSurfaceTextureUpdated(st: SurfaceTexture) = Unit
                }

                exoPlayer.addListener(object : Player.Listener {
                    override fun onVideoSizeChanged(videoSize: VideoSize) {
                        post { scaleToFill(this@apply, videoSize, width, height) }
                    }
                })
            }
        }
    )
}


private fun scaleToFill(view: TextureView, videoSize: VideoSize, viewW: Int, viewH: Int) {
    if (videoSize.width == 0 || videoSize.height == 0 || viewW == 0 || viewH == 0) return

    val scaleX = viewW.toFloat() / videoSize.width
    val scaleY = viewH.toFloat() / videoSize.height
    val scale  = maxOf(scaleX, scaleY)

    val scaledW = videoSize.width  * scale
    val scaledH = videoSize.height * scale

    val dx = (scaledW - viewW) / 2f
    val dy = (scaledH - viewH) / 2f

    val matrix = Matrix()

    matrix.setScale(scaledW / viewW, scaledH / viewH)

    matrix.postTranslate(-dx, -dy)

    view.setTransform(matrix)
}

fun getWeatherVideoRes(weatherId: Int?, dt: Long?, timezone: String = ""): Int {
    val cal = Calendar.getInstance().apply {
        if (timezone.isNotEmpty())
            timeZone = java.util.TimeZone.getTimeZone(timezone)
        if (dt != null)
            timeInMillis = dt * 1000
    }
    val hour    = cal.get(Calendar.HOUR_OF_DAY)
    val isNight = hour < 6 || hour >= 18

    return when (weatherId) {
        800         -> if (isNight) R.raw.clear_night  else R.raw.clear_day
        801, 802,
        803, 804    -> if (isNight) R.raw.clear_night else R.raw.cloudy_day
        in 300..321 -> if (isNight) R.raw.rainy_night  else R.raw.rainy_day
        in 200..232 -> R.raw.thunderstorm
        in 500..531 -> if (isNight) R.raw.rainy_night  else R.raw.rainy_day
        in 600..622 -> R.raw.snowy
        in 700..781 -> R.raw.haze
        else        -> if (isNight) R.raw.clear_night  else R.raw.clear_day
    }
}