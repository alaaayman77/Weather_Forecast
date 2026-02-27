package com.example.weather_forecast

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource


val navigationItems = listOf(
    BottomNavigationBarItem(
        title = "Home",
        icon = R.drawable.ic_home,
        route = NavigationRoutes.HomeRoute
    ),
    BottomNavigationBarItem(
        title = "Favourite",
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
fun BottomNavigationBar( navigate: (BottomNavigationBarItem)->Unit) {

    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }
    NavigationBar(
        containerColor = Color.White
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navigate.invoke(item)

                },
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        color = if (index == selectedNavigationIndex.intValue)
                            Color.Black
                        else Color.Gray
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.surface,
                    indicatorColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}