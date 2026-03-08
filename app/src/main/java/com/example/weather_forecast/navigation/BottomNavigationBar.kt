package com.example.weather_forecast.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.navigation.BottomNavigationBarItem
import com.example.weather_forecast.navigation.NavigationRoutes
import com.example.weather_forecast.R


val navigationItems = listOf(
    BottomNavigationBarItem(
        title = "Weather",
        icon = R.drawable.ic_weather,
        route = NavigationRoutes.WeatherRoute
    ),
    BottomNavigationBarItem(
        title = "Explore",
        icon = R.drawable.ic_favourite,
        route = NavigationRoutes.FavouriteRoute
    ),
    BottomNavigationBarItem(
        title = "Alerts",
        icon = R.drawable.ic_notification,
        route = NavigationRoutes.AlertRoute
    ),
    BottomNavigationBarItem(
        title = "Setting",
        icon = R.drawable.ic_settings,
        route = NavigationRoutes.SettingsRoute
    )
)





@Composable
fun BottomNavigationBar(
    currentRoute: String?,
    navigate: (BottomNavigationBarItem) -> Unit
) {

    val selectedIndex = navigationItems.indexOfFirst { item ->
        currentRoute?.contains(item.route::class.simpleName ?: "") == true
    }.coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation    = 16.dp,
                    shape        = RoundedCornerShape(32.dp),
                    ambientColor = Color(0xFF90CAF9).copy(alpha = 0.4f),
                    spotColor    = Color(0xFF90CAF9).copy(alpha = 0.4f)
                )
                .height(64.dp)
                .clip(RoundedCornerShape(32.dp)),
            containerColor = Color.White.copy(alpha = 0.85f),
            tonalElevation = 0.dp
        ) {
            navigationItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick  = { navigate(item) },
                    icon     = {
                        Icon(
                            painter            = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier           = Modifier.size(24.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor   = Color.White,
                        unselectedIconColor = Color(0xFF90A4AE),
                        indicatorColor      = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}