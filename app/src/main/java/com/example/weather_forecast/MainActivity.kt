package com.example.weather_forecast

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider
import com.example.weather_forecast.data.WeatherRepository
import com.example.weather_forecast.data.datasource.local.WeatherLocalDataSource
import com.example.weather_forecast.data.datasource.remote.WeatherRemoteDataSource
import com.example.weather_forecast.presentation.alerts.AlertViewModel
import com.example.weather_forecast.presentation.alerts.AlertViewModelFactory

import com.example.weather_forecast.presentation.favourite.FavouriteViewModel
import com.example.weather_forecast.presentation.favourite.FavouriteViewModelFactory
import com.example.weather_forecast.presentation.favouriteDetails.FavouriteDetailsViewModel
import com.example.weather_forecast.presentation.favouriteDetails.FavouriteDetailsViewModelFactory
import com.example.weather_forecast.presentation.map.MapPickerViewModel
import com.example.weather_forecast.presentation.map.MapPickerViewModelFactory
import com.example.weather_forecast.presentation.onboarding.OnBoardingViewModel
import com.example.weather_forecast.presentation.onboarding.OnBoardingViewModelFactory
import com.example.weather_forecast.presentation.permission.PermissionViewModel
import com.example.weather_forecast.presentation.settings.SettingsViewModel
import com.example.weather_forecast.presentation.settings.SettingsViewModelFactory
import com.example.weather_forecast.presentation.weather.WeatherViewModel
import com.example.weather_forecast.presentation.weather.WeatherViewModelFactory
import com.example.weather_forecast.presentation.weather.view.components.LottiePreloader
import com.example.weather_forecast.utils.LocationProvider
import com.example.weather_forecast.utils.NotificationPermissionHandler
import com.example.weather_forecast.utils.PermissionHandler
import com.example.weather_forecast.utils.applyLocale
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
    private lateinit var onBoardingViewModel : OnBoardingViewModel
    private lateinit var notificationPermissionHandler: NotificationPermissionHandler



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LottiePreloader.preload(this)
         val localDataSource = WeatherLocalDataSource(application)

         val remoteDataSource = WeatherRemoteDataSource()
        val locationProvider = LocationProvider(
            application,
            LocationServices.getFusedLocationProviderClient(this)
        )

        permissionViewModel = ViewModelProvider(this).get(PermissionViewModel::class.java)

        weatherViewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(locationProvider, WeatherRepository(remoteDataSource , localDataSource), application)
        ).get(WeatherViewModel::class.java)

        favouriteViewModel = ViewModelProvider(
            this,
            FavouriteViewModelFactory(WeatherRepository(remoteDataSource , localDataSource))
        ).get(FavouriteViewModel::class.java)

        mapPickerViewModel = ViewModelProvider(
            this,
            MapPickerViewModelFactory(application, WeatherRepository(remoteDataSource , localDataSource))
        ).get(MapPickerViewModel::class.java)

        favouriteDetailsViewModel = ViewModelProvider(
            this,
            FavouriteDetailsViewModelFactory(WeatherRepository(remoteDataSource , localDataSource), application)
        ).get(FavouriteDetailsViewModel::class.java)
        alertViewModel = ViewModelProvider(
            this,
            AlertViewModelFactory(WeatherRepository(remoteDataSource , localDataSource) , application)
        ).get(AlertViewModel::class.java)
        settingsViewModel = ViewModelProvider(
            this,
            SettingsViewModelFactory(WeatherRepository(remoteDataSource , localDataSource))
        ).get(SettingsViewModel::class.java)
        
        onBoardingViewModel = ViewModelProvider(
            this,
            OnBoardingViewModelFactory(WeatherRepository(remoteDataSource , localDataSource))
        ).get(OnBoardingViewModel::class.java)
        permissionViewModel.resolveInitialState(
            PermissionHandler(this).checkPermissions()
        )
        permissionHandler = PermissionHandler(this).also {
            it.init(permissionViewModel)
        }


        notificationPermissionHandler = NotificationPermissionHandler(this).also {
            it.init()
        }
        val repository = WeatherRepository(remoteDataSource , localDataSource)
        applyLocale(this, repository.getLanguage())
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
                settingsViewModel = settingsViewModel,
                onBoardingViewModel = onBoardingViewModel
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