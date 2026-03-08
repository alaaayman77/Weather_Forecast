package com.example.weather_forecast

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import com.example.weather_forecast.navigation.BottomNavigationBar
import com.example.weather_forecast.navigation.NavigationRoutes
import com.example.weather_forecast.presentation.AlertScreen
import com.example.weather_forecast.presentation.FavouriteDetailsScreen
import com.example.weather_forecast.presentation.SettingsScreen
import com.example.weather_forecast.presentation.SplashScreen
import com.example.weather_forecast.presentation.favourite.FavouriteScreen
import com.example.weather_forecast.presentation.favourite.FavouriteViewModel
import com.example.weather_forecast.presentation.favourite.MapPickerScreen
import com.example.weather_forecast.presentation.map.MapPickerViewModel
import com.example.weather_forecast.presentation.permission.*
import com.example.weather_forecast.presentation.weather.*
import com.example.weather_forecast.ui.theme.Weather_ForecastTheme
import com.example.weather_forecast.utils.PermissionHandler
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun AppScreen(
    weatherViewModel: WeatherViewModel,
    permissionViewModel: PermissionViewModel,
    permissionHandler: PermissionHandler,
    favouriteViewModel: FavouriteViewModel,
    mapPickerViewModel : MapPickerViewModel
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
                            onAddLocation = {
                                mapPickerViewModel.clearPin()
                                val lat = location?.latitude ?: 30.0444
                                val lon = location?.longitude ?: 31.2357
                                navController.navigate(NavigationRoutes.MapPickerRoute(lat, lon))
                            },

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
                            addError         = (addState as? UiState.Error)?.message,
                            onMapTapped      = { mapPickerViewModel.onMapTapped(it) },
                            onPlacePicked    = { latLng, name -> mapPickerViewModel.onPlacePicked(latLng, name) },
                            onClearPin       = { mapPickerViewModel.clearPin() },
                            onLocationPicked = { lat, lng, name ->
                                mapPickerViewModel.onLocationPicked(lat, lng, name, favouriteViewModel)
                            },
                            onDismiss = {
                                mapPickerViewModel.clearPin()
                                navController.popBackStack()
                            }
                        )
                    }

                    composable<NavigationRoutes.AlertRoute> {
                        AlertScreen(modifier = Modifier.padding(innerPadding))
                    }
                    composable<NavigationRoutes.SettingsRoute> {
                        SettingsScreen(modifier = Modifier.padding(innerPadding))
                    }
                    composable<NavigationRoutes.FavouriteDetailsRoute> {it->
                        val lat =  it.toRoute<NavigationRoutes.FavouriteDetailsRoute>().lat
                        val lon = it.toRoute<NavigationRoutes.FavouriteDetailsRoute>().lon
                        FavouriteDetailsScreen(lat = lat , lon = lon ,modifier = Modifier.padding(innerPadding))
                    }
                }
            }
        }
    }
}