package com.example.weather_forecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.presentation.alerts.AlertViewModel
import com.example.weather_forecast.presentation.alerts.AlertViewModelFactory

import com.example.weather_forecast.presentation.favourite.FavouriteViewModel
import com.example.weather_forecast.presentation.favourite.FavouriteViewModelFactory
import com.example.weather_forecast.presentation.favouriteDetails.FavouriteDetailsViewModel
import com.example.weather_forecast.presentation.favouriteDetails.FavouriteDetailsViewModelFactory
import com.example.weather_forecast.presentation.map.MapPickerViewModel
import com.example.weather_forecast.presentation.map.MapPickerViewModelFactory
import com.example.weather_forecast.presentation.permission.PermissionViewModel
import com.example.weather_forecast.presentation.settings.SettingsViewModel
import com.example.weather_forecast.presentation.settings.SettingsViewModelFactory
import com.example.weather_forecast.presentation.weather.WeatherViewModel
import com.example.weather_forecast.presentation.weather.WeatherViewModelFactory
import com.example.weather_forecast.utils.LocationProvider
import com.example.weather_forecast.utils.NotificationPermissionHandler
import com.example.weather_forecast.utils.PermissionHandler
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {

    private lateinit var permissionHandler           : PermissionHandler
    private lateinit var permissionViewModel         : PermissionViewModel
    private lateinit var weatherViewModel            : WeatherViewModel
    private lateinit var favouriteViewModel          : FavouriteViewModel
    private lateinit var mapPickerViewModel          : MapPickerViewModel
    private lateinit var favouriteDetailsViewModel   : FavouriteDetailsViewModel
    private lateinit var alertViewModel   : AlertViewModel
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var notificationPermissionHandler: NotificationPermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val locationProvider = LocationProvider(
            application,
            LocationServices.getFusedLocationProviderClient(this)
        )

        permissionViewModel = ViewModelProvider(this).get(PermissionViewModel::class.java)

        weatherViewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(locationProvider, WeatherRepository(application), application)
        ).get(WeatherViewModel::class.java)

        favouriteViewModel = ViewModelProvider(
            this,
            FavouriteViewModelFactory(WeatherRepository(application))
        ).get(FavouriteViewModel::class.java)

        mapPickerViewModel = ViewModelProvider(
            this,
            MapPickerViewModelFactory(application, WeatherRepository(application))
        ).get(MapPickerViewModel::class.java)

        favouriteDetailsViewModel = ViewModelProvider(
            this,
            FavouriteDetailsViewModelFactory(WeatherRepository(application), application)
        ).get(FavouriteDetailsViewModel::class.java)
        alertViewModel = ViewModelProvider(
            this,
            AlertViewModelFactory(WeatherRepository(application) , application)
        ).get(AlertViewModel::class.java)
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(WeatherRepository(application))
        ).get(SettingsViewModel::class.java)

        permissionHandler = PermissionHandler(this).also {
            it.init(permissionViewModel)
        }

        notificationPermissionHandler = NotificationPermissionHandler(this).also {
            it.init()
        }

        enableEdgeToEdge()
        setContent {
            AppScreen(
                weatherViewModel          = weatherViewModel,
                permissionViewModel       = permissionViewModel,
                permissionHandler         = permissionHandler,
                notificationPermissionHandler = notificationPermissionHandler,
                favouriteViewModel        = favouriteViewModel,
                mapPickerViewModel        = mapPickerViewModel,
                favouriteDetailsViewModel = favouriteDetailsViewModel,
                alertViewModel = alertViewModel,
                settingsViewModel = settingsViewModel
            )


            // request notifications permission for android 13+
            LaunchedEffect(Unit) {
                notificationPermissionHandler.requestIfNeeded()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (permissionHandler.checkPermissions()) {
            permissionViewModel.onPermissionGranted()
        }
    }
}