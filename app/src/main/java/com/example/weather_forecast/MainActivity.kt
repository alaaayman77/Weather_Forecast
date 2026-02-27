package com.example.weather_forecast

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
import androidx.compose.ui.Modifier
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
import com.example.weather_forecast.ui.theme.Weather_ForecastTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController : NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()

            Weather_ForecastTheme {

                Box(modifier = Modifier.fillMaxSize()) {

                    Image(
                        painter = painterResource(id = R.drawable.bg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Scaffold(
                        containerColor = Color.Transparent,
                        bottomBar = {
                            BottomNavigationBar { item ->
                                navController.navigate(item.route)
                            }
                        }
                    ) { innerPadding ->

                        NavHost(
                            navController = navController,
                            startDestination = NavigationRoutes.HomeRoute,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable<NavigationRoutes.HomeRoute> { HomeScreen() }
                            composable<NavigationRoutes.FavouriteRoute> { FavouriteScreen() }
                            composable<NavigationRoutes.AlertRoute> { AlertScreen() }
                            composable<NavigationRoutes.SettingsRoute> { SettingsScreen() }
                        }
                    }
                }
            }
        }
    }
}

