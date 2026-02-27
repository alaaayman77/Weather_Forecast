package com.example.weather_forecast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather_forecast.ui.theme.Weather_ForecastTheme

class MainActivity : ComponentActivity() {
    private lateinit var navController : NavHostController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            navController = rememberNavController()
            Weather_ForecastTheme {
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(){item->
                            navController.navigate(item.route)
                        }
                    }
                )
                { innerPadding ->
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

