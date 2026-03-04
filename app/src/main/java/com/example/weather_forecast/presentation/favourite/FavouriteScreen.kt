package com.example.weather_forecast.presentation.favourite

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.data.models.FavoriteLocationStat
import com.example.weather_forecast.presentation.favourite.components.FavouriteLocationItem

@Composable
fun FavouriteScreen(modifier: Modifier){
    Box (modifier = modifier

    ){
      FavouriteScreenContent()
    }
}
@Composable
fun FavouriteScreenContent(){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(8.dp)
    ){
        item{
            FavouriteHeader()
        }
        items(favouriteLocationStat.size){
                index ->
            FavouriteLocationItem(favouriteLocationStat[index])
        }
    }
}

@Composable
fun FavouriteHeader(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text("Favourite Locations" , style = MaterialTheme.typography.labelLarge.copy(MaterialTheme.colorScheme.secondary))
    }
}

val favouriteLocationStat  = listOf(
    FavoriteLocationStat(Icons.Default.Home , "San Francisco" , 10 , "• Cloudy"),
    FavoriteLocationStat(Icons.Default.Home , "San Francisco" , 10 , "• Cloudy"),
    FavoriteLocationStat(Icons.Default.Home , "San Francisco" , 10 , "• Cloudy"),
    FavoriteLocationStat(Icons.Default.Home , "San Francisco" , 10 , "• Cloudy"),
    FavoriteLocationStat(Icons.Default.Home , "San Francisco" , 10 , "• Cloudy"),


    )