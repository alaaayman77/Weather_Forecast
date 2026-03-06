package com.example.weather_forecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import androidx.lifecycle.ViewModelProvider

import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.utils.PermissionHandler
import com.example.weather_forecast.presentation.permission.PermissionViewModel
import com.example.weather_forecast.utils.LocationProvider
import com.example.weather_forecast.presentation.weather.WeatherViewModel
import com.example.weather_forecast.presentation.weather.WeatherViewModelFactory

import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var permissionHandler: PermissionHandler
    private lateinit var permissionViewModel: PermissionViewModel
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationProvider = LocationProvider(
            application,
            LocationServices.getFusedLocationProviderClient(this)
        )

        permissionViewModel = ViewModelProvider(this).get(PermissionViewModel::class.java)

        weatherViewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(locationProvider, WeatherRepository() ,application )
        ).get(WeatherViewModel::class.java)


        permissionHandler = PermissionHandler(this).also {
            it.init(permissionViewModel)
        }

        enableEdgeToEdge()
        setContent {
            AppScreen(
                weatherViewModel    = weatherViewModel,
                permissionViewModel = permissionViewModel,
                permissionHandler   = permissionHandler
            )
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionHandler.checkPermissions()) {
            permissionViewModel.onPermissionGranted()
        }
    }
}

