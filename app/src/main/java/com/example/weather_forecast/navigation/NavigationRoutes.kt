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
    data class MapPickerRoute(
        val lat: Double,
        val lon: Double,
        val isFromSettings: Boolean = false
    ) : NavigationRoutes()
    @Serializable
    data class FavouriteDetailsRoute(
        val lat : Double,
        val lon : Double
    ) : NavigationRoutes()
}