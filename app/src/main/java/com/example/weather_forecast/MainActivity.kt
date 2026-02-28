package com.example.weather_forecast

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather_forecast.screens.AlertScreen
import com.example.weather_forecast.screens.FavouriteScreen
import com.example.weather_forecast.screens.HomeScreen
import com.example.weather_forecast.screens.SettingsScreen
import com.example.weather_forecast.screens.SplashScreen
import com.example.weather_forecast.ui.theme.Weather_ForecastTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController : NavHostController
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
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
                                    Color(0xFF90CAF9), // Material blue 200
                                    Color(0xFFBBDEFB), // Material blue 100
                                    Color(0xFFE3F2FD)  // Material blue 50
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
                                    navController.navigate(NavigationRoutes.HomeRoute) {
                                        // remove splash from back stack so back button doesn't return to it
                                        popUpTo(NavigationRoutes.SplashRoute) {
                                            inclusive = true
                                        }
                                    }
                                }
                            }
                            composable<NavigationRoutes.HomeRoute> { HomeScreen(modifier = Modifier.padding(innerPadding)) }
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

