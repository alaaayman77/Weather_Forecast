package com.example.weather_forecast.presentation.favourite.view


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weather_forecast.presentation.favourite.view.components.FavouriteLocationItem
import com.example.weather_forecast.presentation.FavouriteState
import com.example.weather_forecast.presentation.UiState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import com.example.weather_forecast.R
import com.example.weather_forecast.ui.theme.lightGray
import com.example.weather_forecast.utils.formatNumber

@Composable
fun FavouriteScreen(
    modifier: Modifier,
    uiState: UiState<FavouriteState>,
    onAddLocation: () -> Unit,
    onRemove : (Double, Double) -> Unit,
    tempUnit: TempUnit,
    windUnit: WindUnit,
    language : Language,
    onNavigateToDetails: (Double , Double) -> Unit,

    ) {
    Box(modifier = modifier.fillMaxSize()
        .statusBarsPadding()) {
        when (val state = uiState) {
            is UiState.Idle,
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            is UiState.Error -> {
                Text(
                    text     = state.message,
                    modifier = Modifier.align(Alignment.Center),
                    style    = MaterialTheme.typography.bodyMedium
                )
            }
            is UiState.Success -> {
                FavouriteScreenContent(
                    favourites = state.data.favourites,
                    onRemove   =onRemove,
                    tempUnit = tempUnit,
                    windUnit = windUnit,
                    onNavigateToDetails = onNavigateToDetails,
                    language = language
                )
            }
        }

        FloatingActionButton(
            onClick        = onAddLocation,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            shape          = CircleShape,
        ) {
            Icon(
                imageVector        = Icons.Default.LocationOn,
                contentDescription = "Add Location",
                tint               = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
@Composable
fun FavouriteScreenContent(
    favourites: List<FavouriteEntity>,
    onRemove: (Double , Double)->Unit,
    tempUnit: TempUnit,
    windUnit: WindUnit,
    onNavigateToDetails : (Double , Double)-> Unit,
    language: Language
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item { FavouriteHeader(locationCount = favourites.size , language) }

        if (favourites.isEmpty()) {
            item { FavouriteEmptyState( modifier = Modifier.fillParentMaxHeight()) }
        } else {
            items(favourites) { item ->
                FavouriteLocationItem(
                    item     = item,
                    onRemove = onRemove,
                    tempUnit = tempUnit,
                    windUnit = windUnit,
                    onNavigateToDetails = onNavigateToDetails,
                    language = language
                )
            }
        }
    }
}
@Composable
fun FavouriteHeader(locationCount: Int = 0, language: Language) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Rounded.Favorite,
                        contentDescription = null,
                        tint               = Color.Red,
                        modifier           = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text  = stringResource(R.string.favourite),
                        style = MaterialTheme.typography.titleMedium.copy(
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 24.sp,
                            letterSpacing = 0.2.sp
                        )
                    )

                }
            }


            if (locationCount > 0) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f))
                ) {
                    Text(
                        text     = formatNumber( locationCount.toString() , language ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style    = MaterialTheme.typography.labelMedium.copy(
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }


        HorizontalDivider(
            thickness = 0.5.dp,
            color     = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        )
    }
}


@Composable
fun FavouriteEmptyState(modifier: Modifier) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center

    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier            = Modifier.padding(horizontal = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier           = Modifier.size(44.dp),
                    tint               = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                text      = stringResource(R.string.no_favourite),
                style     = MaterialTheme.typography.bodyMedium.copy(
                    color      = MaterialTheme.colorScheme.primary,
                    lineHeight = 21.sp
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text      = stringResource(R.string.tap_plus_favourite),
                style     = MaterialTheme.typography.labelSmall.copy(
                    color      = MaterialTheme.colorScheme.primary,
                    lineHeight = 18.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}