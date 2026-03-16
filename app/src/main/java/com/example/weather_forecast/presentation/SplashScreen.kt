package com.example.weather_forecast.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    val scale     = remember { Animatable(0f) }
    val alpha     = remember { Animatable(0f) }
    val offsetY   = remember { Animatable(-80f) }
    val rotation  = remember { Animatable(-15f) }

    LaunchedEffect(Unit) {

        launch {
            scale.animateTo(
                targetValue   = 1f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessLow)
            )
        }
        launch {
            alpha.animateTo(
                targetValue   = 1f,
                animationSpec = tween(durationMillis = 600, easing = EaseOutCubic)
            )
        }
        launch {
            offsetY.animateTo(
                targetValue   = 0f,
                animationSpec = spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessMediumLow)
            )
        }
        launch {
            rotation.animateTo(
                targetValue   = 0f,
                animationSpec = spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessLow)
            )
        }


        delay(300L)
        scale.animateTo(1.05f, tween(500))
        scale.animateTo(1.00f, tween(500))

        delay(1300L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets(0.dp))
    ) {
        Image(
            painter            = painterResource(id = R.drawable.splash_bg),
            contentDescription = null,
            contentScale       = ContentScale.Crop,
            modifier           = Modifier.fillMaxSize()
        )

        Image(
            painter            = painterResource(id = R.drawable.logo2),
            contentDescription = null,
            contentScale       = ContentScale.Fit,
            modifier           = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 24.dp)
                .graphicsLayer {
                    scaleX        = scale.value
                    scaleY        = scale.value
                    this.alpha    = alpha.value
                    translationY  = offsetY.value
                    rotationZ     = rotation.value
                }
        )
    }
}