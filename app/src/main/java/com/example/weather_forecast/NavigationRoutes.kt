package com.example.weather_forecast
import kotlinx.serialization.Serializable

sealed class NavigationRoutes {
    @Serializable
    object HomeRoute : NavigationRoutes()
    @Serializable
    object FavouriteRoute : NavigationRoutes()
    @Serializable
    object AlertRoute : NavigationRoutes()
    @Serializable
    object  SettingsRoute : NavigationRoutes()
    @Serializable
    object  SplashRoute : NavigationRoutes()
}