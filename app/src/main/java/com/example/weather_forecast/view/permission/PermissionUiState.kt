package com.example.weather_forecast.view.permission



sealed class PermissionUiState {
    object Checking : PermissionUiState()
    object Granted : PermissionUiState()
    object Denied : PermissionUiState()
    object PermanentlyDenied : PermissionUiState()
}