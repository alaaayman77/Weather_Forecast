package com.example.weather_forecast

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.navigation.toRoute
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.navigation.BottomNavigationBar
import com.example.weather_forecast.navigation.NavigationRoutes
import com.example.weather_forecast.presentation.alerts.AlertScreen
import com.example.weather_forecast.presentation.favouriteDetails.FavouriteDetailsScreen
import com.example.weather_forecast.presentation.settings.SettingsScreen
import com.example.weather_forecast.presentation.SplashScreen
import com.example.weather_forecast.presentation.alerts.AlertReceiver
import com.example.weather_forecast.presentation.alerts.AlertViewModel
import com.example.weather_forecast.presentation.favourite.FavouriteScreen
import com.example.weather_forecast.presentation.favourite.FavouriteViewModel
import com.example.weather_forecast.presentation.favourite.MapPickerScreen
import com.example.weather_forecast.presentation.favouriteDetails.FavouriteDetailsViewModel
import com.example.weather_forecast.presentation.map.MapPickerViewModel
import com.example.weather_forecast.presentation.permission.*
import com.example.weather_forecast.presentation.settings.SettingsViewModel
import com.example.weather_forecast.presentation.weather.*
import com.example.weather_forecast.ui.theme.Weather_ForecastTheme
import com.example.weather_forecast.utils.AlertStatusReceiver
import com.example.weather_forecast.utils.NotificationPermissionHandler
import com.example.weather_forecast.utils.PermissionHandler
import com.example.weather_forecast.utils.applyLocale
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppScreen(
    weatherViewModel: WeatherViewModel,
    permissionViewModel: PermissionViewModel,
    permissionHandler: PermissionHandler,
    notificationPermissionHandler : NotificationPermissionHandler,
    favouriteViewModel: FavouriteViewModel,
    mapPickerViewModel : MapPickerViewModel,
    favouriteDetailsViewModel: FavouriteDetailsViewModel,
    alertViewModel : AlertViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController : NavHostController  = rememberNavController()
    val context         = LocalContext.current
    val permissionState = permissionViewModel.permissionState.collectAsState()


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute      = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute != null
            && !currentRoute.contains("SplashRoute")
            && !currentRoute.contains("MapPickerRoute")
            && !currentRoute.contains("FavouriteDetailsRoute")


    val location by weatherViewModel.locationState.collectAsStateWithLifecycle()
    val tempUnit    by settingsViewModel.tempUnit.collectAsStateWithLifecycle()
    val windUnit    by settingsViewModel.windUnit.collectAsStateWithLifecycle()
    val language    by settingsViewModel.language.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        notificationPermissionHandler.requestIfNeeded()
    }

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
                containerColor      = Color.Transparent,
                contentWindowInsets = WindowInsets(0),
                bottomBar = {

                    AnimatedVisibility(
                        visible = showBottomBar,
                        enter   = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                        exit    = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                    ) {
                        BottomNavigationBar(
                            currentRoute = currentRoute,
                        ) { item ->
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        }
                    }
                }
            ) { innerPadding ->
                val tempUnit by settingsViewModel.tempUnit.collectAsStateWithLifecycle()
                val windUnit by settingsViewModel.windUnit.collectAsStateWithLifecycle()
                NavHost(
                    navController    = navController,
                    startDestination = NavigationRoutes.SplashRoute,
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
                        val uiState  by weatherViewModel.weatherUiState.collectAsStateWithLifecycle(UiState.Idle)
                        val location by weatherViewModel.locationState.collectAsStateWithLifecycle()

                        val language    by settingsViewModel.language.collectAsStateWithLifecycle()
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
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding),
                                    uiState  = uiState,
                                    location = location,
                                    tempUnit = tempUnit,
                                    windUnit = windUnit,
                                    language = language
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
                        val uiState by favouriteViewModel.uiState.collectAsStateWithLifecycle(UiState.Idle)
                        FavouriteScreen(
                            modifier      = Modifier.padding(innerPadding),
                            uiState       = uiState,
                            onRemove = { lat, lon -> favouriteViewModel.removeFavourite(lat, lon) },
                            onAddLocation = {
                                mapPickerViewModel.clearPin()
                                val lat = location?.latitude ?: 30.0444
                                val lon = location?.longitude ?: 31.2357
                                navController.navigate(NavigationRoutes.MapPickerRoute(lat, lon))
                            },
                            tempUnit = tempUnit,
                            windUnit = windUnit,
                            language = language,


                        ){ lat , lon ->
                            navController.navigate(
                                NavigationRoutes.FavouriteDetailsRoute(lat ,lon)
                            )
                        }
                    }


                    composable<NavigationRoutes.MapPickerRoute> {
                        val route        = it.toRoute<NavigationRoutes.MapPickerRoute>()
                        val pickedLatLng by mapPickerViewModel.pickedLatLng.collectAsStateWithLifecycle()
                        val pickedName   by mapPickerViewModel.pickedName.collectAsStateWithLifecycle()
                        val addState     by mapPickerViewModel.addState.collectAsStateWithLifecycle()

                        var hasNavigatedBack by remember { mutableStateOf(false) }

                        LaunchedEffect(addState) {
                            if (addState is UiState.Success && !hasNavigatedBack) {
                                hasNavigatedBack = true
                                pickedLatLng?.let { latLng ->

                                }
                                navController.popBackStack()
                            }
                        }

                        val userLatLng = remember { LatLng(route.lat, route.lon) }

                        val cameraState = rememberCameraPositionState {
                            position = CameraPosition.fromLatLngZoom(userLatLng, 12f)
                        }

                        LaunchedEffect(Unit) {
                            mapPickerViewModel.onMapTapped(userLatLng)
                        }

                        LaunchedEffect(pickedLatLng) {
                            pickedLatLng?.let { latLng ->
                                cameraState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 14f))
                            }
                        }

                        MapPickerScreen(
                            pickedLatLng     = pickedLatLng,
                            pickedName       = pickedName,
                            cameraState      = cameraState,
                            isAddingLocation = addState is UiState.Loading,
                            isFromSettings  = route.isFromSettings,
                            addError         = (addState as? UiState.Error)?.message,
                            onMapTapped      = { mapPickerViewModel.onMapTapped(it) },
                            onPlacePicked    = { latLng, name -> mapPickerViewModel.onPlacePicked(latLng, name) },
                            onClearPin       = { mapPickerViewModel.clearPin() },
                            onLocationPicked = { lat, lng, name ->
                                mapPickerViewModel.onLocationPicked(lat, lng, name, favouriteViewModel)
                            },
                            onLocationChosen = { lat, lon ->
                                settingsViewModel.saveManualLocation(lat, lon)
                                weatherViewModel.checkLocationAndFetch()
                                navController.popBackStack()
                            },
                            onDismiss = {
                                mapPickerViewModel.clearPin()
                                navController.popBackStack()
                            },
                            onZoomIn = { mapPickerViewModel.zoomIn(cameraState) },
                            onZoomOut = { mapPickerViewModel.zoomOut(cameraState) }
                        )
                    }

                    composable<NavigationRoutes.AlertRoute> {
                        val uiState         by alertViewModel.weatherAlertsState.collectAsStateWithLifecycle(UiState.Idle)
                        val scheduledAlerts by alertViewModel.scheduledAlerts.collectAsStateWithLifecycle()

                        val showBottomSheet by alertViewModel.showBottomSheet.collectAsStateWithLifecycle()
                        val showPermDialog  by alertViewModel.showPermDialog.collectAsStateWithLifecycle()
                        val alertStatuses   by alertViewModel.alertStatuses.collectAsStateWithLifecycle()

                        // register status receiver only while on this screen
                        DisposableEffect(Unit) {
                            val receiver = AlertStatusReceiver { alertId, status ->
                                alertViewModel.updateAlertStatus(alertId, status)
                            }
                            val filter = IntentFilter(AlertReceiver.ACTION_STATUS_UPDATE)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
                            } else {
                                context.registerReceiver(
                                    receiver,
                                    filter,
                                    Context.RECEIVER_NOT_EXPORTED
                                )
                            }
                            onDispose { context.unregisterReceiver(receiver) }
                        }

                        LaunchedEffect(location) {
                            val lat = 40.1934
                            val lon = -85.3864
                            alertViewModel.fetchWeatherAlerts(lat, lon)
                        }

                        AlertScreen(
                            modifier            = Modifier.padding(innerPadding),
                            weatherAlertsState  = uiState,
                            scheduledAlerts     = scheduledAlerts,
                            alertStatuses       = alertStatuses,
                            showBottomSheet     = showBottomSheet,
                            showPermDialog      = showPermDialog,
                            canScheduleExact    = alertViewModel.canScheduleExactAlarms(),
                            onRetry             = { alertViewModel.fetchWeatherAlerts(40.1934, -85.3864) },
                            onCancelAlert       = { alertViewModel.cancelAlert(it) },
                            onScheduleAlert     = { type, startMillis, endMillis, startLabel, endLabel ->
                                alertViewModel.scheduleAlert(type, startMillis, endMillis, startLabel, endLabel)
                            },

                            onFabClicked        = { alertViewModel.onFabClicked() },
                            onDismissSheet      = { alertViewModel.onDismissBottomSheet() },
                            onDismissPermDialog = { alertViewModel.onDismissPermDialog() }
                        )
                    }
                    composable<NavigationRoutes.SettingsRoute> {
                        val tempUnit    by settingsViewModel.tempUnit.collectAsStateWithLifecycle()
                        val windUnit    by settingsViewModel.windUnit.collectAsStateWithLifecycle()
                        val locationSrc by settingsViewModel.locationSource.collectAsStateWithLifecycle()
                        val language    by settingsViewModel.language.collectAsStateWithLifecycle()
                        SettingsScreen(
                            modifier = Modifier.padding(innerPadding),
                            tempUnit = tempUnit,
                            onTempUnitClick = {settingsViewModel.setTempUnit(it)},
                            windUnit = windUnit,
                            onWindUnitClick = { settingsViewModel.setWindUnit(it) },
                            locationSrc = locationSrc,
                            onLocationSourceGPSClick = {
                                settingsViewModel.setLocationSource(LocationSource.GPS)
                                weatherViewModel.updateLocationSource(LocationSource.GPS)
                                weatherViewModel.checkLocationAndFetch()
                            },
                            onLocationSourceMAPClick = {
                                settingsViewModel.setLocationSource(LocationSource.MAP)
                                weatherViewModel.updateLocationSource(LocationSource.MAP)
                                val lat = weatherViewModel.locationState.value.latitude.takeIf { it != 0.0 } ?: 30.0444
                                val lon = weatherViewModel.locationState.value.longitude.takeIf { it != 0.0 } ?: 31.2357
                                mapPickerViewModel.clearPin()
                                navController.navigate(NavigationRoutes.MapPickerRoute(lat, lon, isFromSettings = true))
                            },
                            language = language,
                            onLanguageClick = { settingsViewModel.setLanguage(it)
                                weatherViewModel.onLanguageChanged()
                                applyLocale(context, it)
                                (context as? Activity)?.recreate() }
                            )
                    }
                    composable<NavigationRoutes.FavouriteDetailsRoute> {
                        val route    = it.toRoute<NavigationRoutes.FavouriteDetailsRoute>()
                        val uiState by favouriteDetailsViewModel.uiState.collectAsState()
                        val language    by settingsViewModel.language.collectAsStateWithLifecycle()

                        LaunchedEffect(route.lat, route.lon) {
                            favouriteDetailsViewModel.fetchWeather(route.lat, route.lon)
                        }

                        FavouriteDetailsScreen(
                            modifier = Modifier.padding(innerPadding),
                            lat      = route.lat,
                            lon      = route.lon,
                            uiState  = uiState,
                            onBack   = { navController.popBackStack() },
                            tempUnit = tempUnit,
                            windUnit = windUnit,
                            language = language
                        )
                    }
                }
            }
        }
    }
}