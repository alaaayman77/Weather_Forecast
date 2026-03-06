package com.example.weather_forecast.presentation.weather.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.R
import com.example.weather_forecast.ui.theme.Poppins

@Composable
fun SunTimesCard(
    sunriseText: String = "6:12 AM",
    sunsetText: String  = "6:48 PM",
    // 0f = sunrise, 1f = sunset, 0.5f = solar noon
    progress: Float = 0.6f
) {
    Card(
        modifier = Modifier.fillMaxWidth(),

        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.55f))
    ) {
        Column(modifier = Modifier.padding(32.dp)) {

            Text(
                text = "SUN TIMES",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp,
                    fontFamily = Poppins
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                // Dashed arc
                val arcColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                val sunColor  = Color(0xFFFFC107)

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val arcLeft   = w * 0.08f
                    val arcRight  = w * 0.92f
                    val arcWidth  = arcRight - arcLeft
                    val arcHeight = h * 1.6f
                    val arcTop    = h - arcHeight + h * 0.3f


                    drawArc(
                        color = arcColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(arcLeft, arcTop),
                        size = androidx.compose.ui.geometry.Size(arcWidth, arcHeight),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f))
                        )
                    )


                    val angle = Math.toRadians((180 + progress * 180).toDouble())
                    val cx = arcLeft + arcWidth / 2
                    val cy = arcTop + arcHeight / 2
                    val rx = arcWidth / 2
                    val ry = arcHeight / 2
                    val sunX = (cx + rx * Math.cos(angle)).toFloat()
                    val sunY = (cy + ry * Math.sin(angle)).toFloat()

                    drawCircle(
                        color = sunColor,
                        radius = 10.dp.toPx(),
                        center = Offset(sunX, sunY)
                    )
                    drawCircle(
                        color = sunColor.copy(alpha = 0.3f),
                        radius = 16.dp.toPx(),
                        center = Offset(sunX, sunY)
                    )
                }


                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    horizontalAlignment = Alignment.Start
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sunrise),
                        contentDescription = null,
                        tint = Color.Unspecified

                    )
                    Text(
                        text = "SUNRISE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                            letterSpacing = 1.sp,
                            fontFamily = Poppins
                        )
                    )
                    Text(
                        text = sunriseText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins
                        )
                    )
                }


                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_sunset),
                        contentDescription = null,
                        tint = Color.Unspecified

                        )
                    Text(
                        text = "SUNSET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                            letterSpacing = 1.sp,
                            fontFamily = Poppins
                        )
                    )
                    Text(
                        text = sunsetText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Poppins
                        )
                    )
                }
            }
        }
    }
}