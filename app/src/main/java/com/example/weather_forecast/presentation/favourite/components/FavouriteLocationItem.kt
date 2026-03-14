package com.example.weather_forecast.presentation.favourite.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather_forecast.R
import com.example.weather_forecast.data.models.FavouriteEntity
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import com.example.weather_forecast.ui.theme.lightGray
import com.example.weather_forecast.utils.formatTemp

@Composable
fun FavouriteLocationItem(
    item: FavouriteEntity,
    onRemove: (Double, Double) -> Unit,
    tempUnit: TempUnit,
    windUnit: WindUnit,
    onNavigateToDetails : (Double,Double)->Unit,
    language : Language
) {
    val current = item.oneCallResponse.current
    val daily   = item.oneCallResponse.daily.firstOrNull()

    val highTemp         =  "${formatTemp(UnitConverter.convertTemp(daily?.temp?.max ?:0.0, tempUnit), tempUnit , language)}"
    val lowTemp          =   "${formatTemp(UnitConverter.convertTemp(daily?.temp?.min ?:0.0, tempUnit), tempUnit , language)}"
    val weatherCondition = current.weather.firstOrNull()?.description
        ?.replaceFirstChar { it.uppercase() } ?: ""
    val iconUrl          = current.weather.firstOrNull()?.iconUrl()

    Card(
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f)),
        onClick = { onNavigateToDetails.invoke(item.lat , item.lon) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {

            IconButton(
                onClick = { onRemove(item.lat , item.lon) },
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(R.drawable.delete),
                    contentDescription = "Remove",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.cityName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "${item.countryName} · ${item.countryCode}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
                        )
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = highTemp,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        )
                        Text(
                            text = weatherCondition,
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                            )
                        )
                    }


                    iconUrl?.let { url ->
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }


                HorizontalDivider(
                    color = Color.White.copy(alpha = 0.3f),
                    thickness = 0.8.dp
                )


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_humid),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${current.humidity}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_wind),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text =  "${UnitConverter.convertWind(current.wind_speed, windUnit)}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "H ",
                            style = MaterialTheme.typography.labelSmall.copy(color = lightGray)
                        )
                        Text(
                            text = "${highTemp}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = " L ",
                            style = MaterialTheme.typography.labelSmall.copy(color = lightGray)
                        )
                        Text(
                            text = "${lowTemp}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}


