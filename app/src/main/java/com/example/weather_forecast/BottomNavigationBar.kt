package com.example.weather_forecast

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp


val navigationItems = listOf(
    BottomNavigationBarItem(
        title = "Home",
        icon = R.drawable.ic_home,
        route = NavigationRoutes.HomeRoute
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
fun BottomNavigationBar(navigate: (BottomNavigationBarItem) -> Unit) {

    val selectedNavigationIndex = rememberSaveable { mutableIntStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        NavigationBar(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(32.dp),
                    ambientColor = Color(0xFF90CAF9).copy(alpha = 0.4f),
                    spotColor = Color(0xFF90CAF9).copy(alpha = 0.4f)
                )
                .height(80.dp)
                .clip(RoundedCornerShape(32.dp)),

            containerColor = Color.White.copy(alpha = 0.85f),
            tonalElevation = 0.dp

        ) {
            navigationItems.forEachIndexed { index, item ->
                val isSelected = selectedNavigationIndex.intValue == index

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        selectedNavigationIndex.intValue = index
                        navigate.invoke(item)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = item.title,
                            modifier = Modifier.size(32.dp)
                        )
                    },

                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color(0xFF90A4AE),
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}