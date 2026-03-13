package com.example.weather_forecast.presentation.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.presentation.settings.components.InfoCard
import com.example.weather_forecast.presentation.settings.components.LocationOptionCard
import com.example.weather_forecast.presentation.settings.components.SettingsSection
import com.example.weather_forecast.presentation.settings.components.UnitChip
import com.example.weather_forecast.ui.theme.lightGray
import com.example.weather_forecast.utils.getDisplayName


@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    tempUnit : TempUnit,
    onTempUnitClick :(TempUnit) -> Unit,
    windUnit : WindUnit,
    onWindUnitClick : (WindUnit)-> Unit,
    locationSrc : LocationSource,
    onLocationSourceGPSClick : (LocationSource)-> Unit,
    onLocationSourceMAPClick : (LocationSource)-> Unit,
    language : Language,
    onLanguageClick : (Language)-> Unit
) {

    var langExpanded by remember { mutableStateOf(false) }
    val languages = Language.entries

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text  = "Settings",
            style = MaterialTheme.typography.titleLarge.copy(
                color      = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
                fontSize   = 22.sp
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        SettingsSection(icon = Icons.Rounded.LocationOn, title = "Location") {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LocationOptionCard(
                    title    = "Use GPS",
                    subtitle = "Current location via satellite",
                    selected = locationSrc == LocationSource.GPS,
                    onClick  = { onLocationSourceGPSClick(LocationSource.GPS) }
                )
                LocationOptionCard(
                    title    = "Choose from Map",
                    subtitle = "Select manually on world map",
                    selected = locationSrc == LocationSource.MAP,
                    onClick  = { onLocationSourceMAPClick(LocationSource.MAP) }
                )
            }
        }


        SettingsSection(icon = Icons.Rounded.Settings, title = "Units") {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text  = "TEMPERATURE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = lightGray, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold
                        )
                    )
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement   = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TempUnit.entries.forEach { unit ->
                            UnitChip(
                                label = when (unit) {
                                    TempUnit.CELSIUS    -> "Celsius (°C)"
                                    TempUnit.FAHRENHEIT -> "Fahrenheit (°F)"
                                    TempUnit.KELVIN     -> "Kelvin (K)"
                                },
                                selected = tempUnit == unit,
                                onClick  = { onTempUnitClick(unit) }
                            )
                        }

                    }
                }


                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text  = "WIND SPEED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = lightGray, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WindUnit.entries.forEach { unit ->
                            UnitChip(
                                label = when (unit) {
                                    WindUnit.MS  -> "m/s"
                                    WindUnit.MPH -> "mph"
                                    WindUnit.KMH -> "km/h"
                                },
                                selected = windUnit == unit,
                                onClick  = {onWindUnitClick(unit)}
                            )
                        }
                    }
                }
            }
        }


        SettingsSection(icon = Icons.Default.Home, title = "Language") {
            Box {
                Surface(
                    modifier       = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).clickable { langExpanded = true },
                    shape          = RoundedCornerShape(14.dp),
                    color          = Color.White.copy(alpha = 0.55f),
                    border         = BorderStroke(1.dp, Color.White.copy(alpha = 0.70f)),
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            text  = getDisplayName(language),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium
                            )
                        )
                        Icon(
                            imageVector        = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = null,
                            tint               = lightGray,
                            modifier           = Modifier.size(20.dp)
                        )
                    }
                }
                DropdownMenu(expanded = langExpanded, onDismissRequest = { langExpanded = false }) {

                    languages.forEach { lang ->
                        DropdownMenuItem(
                            text    = { Text(getDisplayName(lang)) },
                            onClick = {
                               onLanguageClick(lang)
                                langExpanded = false }
                        )
                    }
                }
            }
        }
        InfoCard()
        Spacer(Modifier.height(16.dp))
    }
}
