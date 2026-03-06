package com.example.weather_forecast

import android.content.Intent
import android.location.Location
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.weather_forecast.navigation.BottomNavigationBar
import com.example.weather_forecast.navigation.NavigationRoutes
import com.example.weather_forecast.presentation.AlertScreen
import com.example.weather_forecast.presentation.SettingsScreen
import com.example.weather_forecast.presentation.SplashScreen
import com.example.weather_forecast.presentation.favourite.FavouriteScreen
import com.example.weather_forecast.presentation.permission.LoadingScreen
import com.example.weather_forecast.presentation.permission.PermissionDeniedScreen
import com.example.weather_forecast.presentation.permission.PermissionRationaleScreen
import com.example.weather_forecast.presentation.permission.PermissionUiState
import com.example.weather_forecast.presentation.permission.PermissionViewModel
import com.example.weather_forecast.presentation.weather.UiState
import com.example.weather_forecast.presentation.weather.WeatherScreen

import com.example.weather_forecast.presentation.weather.WeatherViewModel
import com.example.weather_forecast.ui.theme.Weather_ForecastTheme
import com.example.weather_forecast.utils.PermissionHandler

@Composable
fun AppScreen(
    weatherViewModel: WeatherViewModel,
    permissionViewModel: PermissionViewModel,
    permissionHandler: PermissionHandler
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val permissionState = permissionViewModel.permissionState.collectAsState()

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.value?.destination?.route
    val showBottomBar = currentRoute?.contains("SplashRoute") == false

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
        ) {

            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets(0),
                bottomBar = {
                    if (showBottomBar) {
                        BottomNavigationBar { item ->
                            navController.navigate(item.route)
                        }
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController    = navController,
                    startDestination = NavigationRoutes.SplashRoute,
                    modifier         = Modifier
                ) {
                    composable<NavigationRoutes.SplashRoute> {
                        SplashScreen {
                            navController.navigate(NavigationRoutes.WeatherRoute) {
                                popUpTo(NavigationRoutes.SplashRoute) { inclusive = true }
                            }
                            if (permissionHandler.checkPermissions()) {
                                permissionViewModel.onPermissionAlreadyGranted()

                            } else {
                                permissionHandler.requestPermissions()
                            }
                        }
                    }

                    composable<NavigationRoutes.WeatherRoute> {
                        val uiState by weatherViewModel.weatherUiState.collectAsStateWithLifecycle(
                            UiState.Idle)
                        val location by weatherViewModel.locationState.collectAsStateWithLifecycle()

                        when (permissionState.value) {
                            PermissionUiState.Granted -> {
                                LaunchedEffect(Unit) {
                                    weatherViewModel.openLocationSettings.collect {
                                        context.startActivity(
                                            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                                                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            }
                                        )
                                    }
                                }

                                LaunchedEffect(Unit) {
                                    weatherViewModel.checkLocationAndFetch()
                                }


                                WeatherScreen(
                                    modifier  = Modifier.fillMaxSize()
                                        .padding(innerPadding),
                                    uiState   = uiState,
                                    location  = location,
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
                                        context.startActivity(
                                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                data = Uri.fromParts("package", context.packageName, null)
                                            }
                                        )
                                    }
                                )
                            }
                            PermissionUiState.Checking -> LoadingScreen()
                        }
                    }

                    composable<NavigationRoutes.FavouriteRoute> {
                        FavouriteScreen(modifier = Modifier.padding(innerPadding))
                    }
                    composable<NavigationRoutes.AlertRoute> {
                        AlertScreen(modifier = Modifier.padding(innerPadding))
                    }
                    composable<NavigationRoutes.SettingsRoute> {
                        SettingsScreen(modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}