package com.example.weather_forecast.view.permission

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PermissionViewModel : ViewModel() {

    private val _permissionState = MutableStateFlow<PermissionUiState>(PermissionUiState.Checking)
    val permissionState: StateFlow<PermissionUiState>
        get()= _permissionState


    fun onPermissionGranted() {
        _permissionState.value = PermissionUiState.Granted
    }

    fun onPermissionDenied() {
        _permissionState.value = PermissionUiState.Denied
    }

    fun onPermissionPermanentlyDenied() {
        _permissionState.value = PermissionUiState.PermanentlyDenied
    }

    fun onPermissionAlreadyGranted() {
        _permissionState.value = PermissionUiState.Granted
    }
}