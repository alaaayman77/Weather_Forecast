package com.example.weather_forecast

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather_forecast.utils.PermissionHandler
import com.example.weather_forecast.navigation.BottomNavigationBar
import com.example.weather_forecast.navigation.NavigationRoutes
import com.example.weather_forecast.presentation.AlertScreen
import com.example.weather_forecast.presentation.favourite.FavouriteScreen

import com.example.weather_forecast.presentation.SettingsScreen
import com.example.weather_forecast.presentation.SplashScreen
import com.example.weather_forecast.ui.theme.Weather_ForecastTheme
import com.example.weather_forecast.presentation.permission.LoadingScreen
import com.example.weather_forecast.presentation.permission.PermissionDeniedScreen
import com.example.weather_forecast.presentation.permission.PermissionRationaleScreen
import com.example.weather_forecast.presentation.permission.PermissionUiState
import com.example.weather_forecast.presentation.permission.PermissionViewModel
import com.example.weather_forecast.utils.LocationProvider

import com.example.weather_forecast.presentation.weather.WeatherScreen
import com.example.weather_forecast.presentation.weather.WeatherViewModel
import com.example.weather_forecast.presentation.weather.WeatherViewModelFactory
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    private lateinit var navController : NavHostController
    private lateinit var permissionHandler: PermissionHandler
    private lateinit var permissionViewModel: PermissionViewModel

    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionViewModel = ViewModelProvider(this)
            .get(PermissionViewModel::class.java)
        val locationProvider = LocationProvider(
            application,
            LocationServices.getFusedLocationProviderClient(this)
        )

        permissionHandler = PermissionHandler(this).also {
            it.init(permissionViewModel)
        }


        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()

            val permissionState = permissionViewModel.permissionState.collectAsState()
            viewModel = viewModel(
                factory = WeatherViewModelFactory(locationProvider)
            )


            val currentRoute = navController.currentBackStackEntryFlow
                .collectAsState(initial = navController.currentBackStackEntry)

            val showBottomBar = currentRoute.value?.destination?.route
                ?.contains("SplashRoute") == false

            Weather_ForecastTheme {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF90CAF9),
                                    Color(0xFFBBDEFB),
                                    Color(0xFFE3F2FD)
                                )
                            )
                        )
                ){



                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = {
                            if(showBottomBar){
                                BottomNavigationBar { item ->
                                    navController.navigate(item.route)
                                }
                            }

                        }
                    ) { innerPadding ->

                        NavHost(
                            navController = navController,
                            startDestination = NavigationRoutes.SplashRoute,
                            modifier = Modifier
                        ) {
                            composable<NavigationRoutes.SplashRoute> {
                                SplashScreen {
                                    navController.navigate(NavigationRoutes.WeatherRoute) {
                                        // remove splash from back stack so back button doesn't return to it
                                        popUpTo(NavigationRoutes.SplashRoute) {
                                            inclusive = true
                                        }
                                        if (permissionHandler.checkPermissions()) {
                                            permissionViewModel.onPermissionAlreadyGranted()
                                            viewModel.checkLocationAndFetch()
                                        } else {
                                            permissionHandler.requestPermissions()
                                        }
                                    }
                                }
                            }
                            composable<NavigationRoutes.WeatherRoute> {
                                when (permissionState.value) {
                                    PermissionUiState.Granted -> {
                                        LaunchedEffect(Unit) {
                                            viewModel.checkLocationAndFetch()
                                        }
                                        WeatherScreen(
                                            modifier = Modifier.padding(innerPadding),
                                            location = viewModel.locationState.observeAsState().value?:Location("")
                                        )
                                    }
                                    PermissionUiState.Denied -> {
                                        PermissionRationaleScreen(
                                            onRetry = { permissionHandler.requestPermissions() }
                                        )
                                    }
                                    PermissionUiState.PermanentlyDenied -> {
                                        PermissionDeniedScreen(
                                            onOpenSettings = {
                                                startActivity(
                                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                        data = Uri.fromParts("package", packageName, null)
                                                    }
                                                )
                                            }
                                        )
                                    }
                                    PermissionUiState.Checking -> {
                                        LoadingScreen()
                                    }
                                }
                            }

                            composable<NavigationRoutes.FavouriteRoute> { FavouriteScreen(modifier = Modifier.padding(innerPadding)) }
                            composable<NavigationRoutes.AlertRoute> { AlertScreen(modifier = Modifier.padding(innerPadding)) }
                            composable<NavigationRoutes.SettingsRoute> { SettingsScreen(modifier = Modifier.padding(innerPadding)) }
                        }
                    }
                }
            }
        }
    }

}

