package com.example.weather_forecast.navigation

import kotlinx.serialization.Serializable

sealed class NavigationRoutes {
    @Serializable
    object WeatherRoute : NavigationRoutes()
    @Serializable
    object FavouriteRoute : NavigationRoutes()
    @Serializable
    object AlertRoute : NavigationRoutes()
    @Serializable
    object  SettingsRoute : NavigationRoutes()
    @Serializable
    object  SplashRoute : NavigationRoutes()
    @Serializable
    object MapPickerRoute : NavigationRoutes()
}