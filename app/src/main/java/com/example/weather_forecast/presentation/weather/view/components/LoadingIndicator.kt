package com.example.weather_forecast.presentation.weather.view.components



import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.example.weather_forecast.R

@Composable
fun LoadingIndicator(
    modifier: Modifier = Modifier,
    size    : Dp       = 500.dp
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations  = LottieConstants.IterateForever
    )
    LottieAnimation(
        composition = composition,
        progress    = { progress },
        modifier    = modifier.size(size)
    )
}