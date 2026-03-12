package com.example.weather_forecast.presentation.favourite


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
import com.example.weather_forecast.presentation.favourite.components.FavouriteLocationItem
import com.example.weather_forecast.presentation.weather.FavouriteState
import com.example.weather_forecast.presentation.weather.UiState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.ui.theme.lightGray

@Composable
fun FavouriteScreen(
    modifier: Modifier,
    uiState: UiState<FavouriteState>,
    onAddLocation: () -> Unit,
    onRemove : (Double, Double) -> Unit,
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
                    onNavigateToDetails = onNavigateToDetails
                )
            }
        }

        FloatingActionButton(
            onClick        = onAddLocation,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary
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
    onNavigateToDetails : (Double , Double)-> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
    ) {
        item { FavouriteHeader(locationCount = favourites.size) }

        if (favourites.isEmpty()) {
            item { FavouriteEmptyState() }
        } else {
            items(favourites) { item ->
                FavouriteLocationItem(
                    item     = item,
                    onRemove = onRemove,
                    onNavigateToDetails = onNavigateToDetails
                )
            }
        }
    }
}
@Composable
fun FavouriteHeader(locationCount: Int = 0) {
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
                        tint               = MaterialTheme.colorScheme.primary,
                        modifier           = Modifier.size(20.dp)
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text  = "Favourite Locations",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color      = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.2.sp
                        )
                    )

                }
            }

            // Count badge — only shown when there are locations
            if (locationCount > 0) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.20f))
                ) {
                    Text(
                        text     = locationCount.toString(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style    = MaterialTheme.typography.labelMedium.copy(
                            color      = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // ── Subtle divider ────────────────────────────────────────────────────
        HorizontalDivider(
            thickness = 0.5.dp,
            color     = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
        )
    }
}


@Composable
fun FavouriteEmptyState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector        = Icons.Default.LocationOn,
                contentDescription = null,
                modifier           = Modifier.size(56.dp),
                tint               = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
            )
            Text(
                text  = "No favourite locations yet",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            )
            Text(
                text  = "Tap + to add one",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                )
            )
        }
    }
}