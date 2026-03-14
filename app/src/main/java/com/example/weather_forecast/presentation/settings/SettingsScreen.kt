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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.Language
import com.example.weather_forecast.data.models.TempUnit
import com.example.weather_forecast.data.models.WindUnit
import com.example.weather_forecast.data.models.LocationSource
import com.example.weather_forecast.presentation.settings.components.LocationOptionCard
import com.example.weather_forecast.presentation.settings.components.SettingsSection
import com.example.weather_forecast.presentation.settings.components.UnitChip
import com.example.weather_forecast.ui.theme.lightGray
import com.example.weather_forecast.utils.getDisplayName
import com.example.weather_forecast.R


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
            text  =stringResource(R.string.settings),
            style = MaterialTheme.typography.labelLarge.copy(
                color      = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize   = 24.sp
            ),
            modifier = Modifier.padding(top = 8.dp)
        )

        SettingsSection(icon = R.drawable.ic_location, title = stringResource(R.string.location)) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                LocationOptionCard(
                    title    = stringResource(R.string.use_gps),
                    subtitle = stringResource(R.string.use_gps_subtitle),
                    selected = locationSrc == LocationSource.GPS,
                    onClick  = { onLocationSourceGPSClick(LocationSource.GPS) }
                )
                LocationOptionCard(
                    title    =stringResource(R.string.choose_from_map),
                    subtitle =stringResource(R.string.choose_from_map_subtitle),
                    selected = locationSrc == LocationSource.MAP,
                    onClick  = { onLocationSourceMAPClick(LocationSource.MAP) }
                )
            }
        }


        SettingsSection(icon = R.drawable.ic_unit, title = stringResource(R.string.units)) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text  = stringResource(R.string.temperature),
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
                                    TempUnit.CELSIUS    ->stringResource(R.string.celsius)
                                    TempUnit.FAHRENHEIT -> stringResource(R.string.fahrenheit)
                                    TempUnit.KELVIN     -> stringResource(R.string.kelvin)
                                },
                                selected = tempUnit == unit,
                                onClick  = { onTempUnitClick(unit) }
                            )
                        }

                    }
                }


                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text  = stringResource(R.string.wind_speed),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = lightGray, letterSpacing = 1.sp, fontWeight = FontWeight.SemiBold
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WindUnit.entries.forEach { unit ->
                            UnitChip(
                                label = when (unit) {
                                    WindUnit.MS  -> stringResource(R.string.wind_ms)
                                    WindUnit.MPH -> stringResource(R.string.wind_mph)
                                    WindUnit.KMH -> stringResource(R.string.wind_kmh)
                                },
                                selected = windUnit == unit,
                                onClick  = {onWindUnitClick(unit)}
                            )
                        }
                    }
                }
            }
        }


        SettingsSection(icon = R.drawable.ic_globe, title =stringResource(R.string.language)) {
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

        Spacer(Modifier.height(16.dp))
    }
}
