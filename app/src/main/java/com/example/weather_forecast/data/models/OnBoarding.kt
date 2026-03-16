package com.example.weather_forecast.data.models



import androidx.annotation.RawRes
import com.example.weather_forecast.R

data class OnboardingPage(
    val title     : String,
    val subtitle  : String,
    val tag       : String,
    @RawRes val gifRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        title      = "Always up to date",
        subtitle   = "Live forecasts around the clock — from sunrise to starlight",
        tag        = "Day & Night",
        gifRes  = R.raw.sun_and_moon
    ),
    OnboardingPage(
        title      = "Storm alerts, instantly",
        subtitle   = "Get warned before thunder strikes — set alerts for any condition",
        tag        = "Severe Weather",
        gifRes  = R.raw.angry_cloud
    ),
    OnboardingPage(
        title      = "See through the haze",
        subtitle   = "Air quality, fog and visibility data — all in one place",
        tag        = "Visibility",
        gifRes  = R.raw.wind_haze
    )
)