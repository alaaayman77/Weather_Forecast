package com.example.weather_forecast.presentation.weather.view.components


import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest

@Composable
fun WeatherGifImage(
    @RawRes gifRes  : Int,
    modifier        : Modifier = Modifier
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(gifRes)
            .decoderFactory(GifDecoder.Factory())
            .build(),
        contentDescription = null,
        modifier           = modifier
    )
}