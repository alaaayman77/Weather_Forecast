package com.example.weather_forecast.presentation.alerts

import android.app.TimePickerDialog
import android.os.Build
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.AlertItem
import com.example.weather_forecast.data.models.AlertTab
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.data.models.WeatherAlert
import com.example.weather_forecast.presentation.alerts.components.AlarmPermissionDialog
import com.example.weather_forecast.presentation.alerts.components.SectionHeader
import com.example.weather_forecast.presentation.weather.UiState
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(
    modifier          : Modifier  = Modifier,
    weatherAlertsState: UiState<List<WeatherAlert>>,
    scheduledAlerts   : List<AlertItem>,
    selectedTab       : AlertTab,
    showBottomSheet   : Boolean,
    showPermDialog    : Boolean,
    canScheduleExact  : Boolean,
    onRetry           : () -> Unit,
    onCancelAlert     : (AlertItem) -> Unit,
    onScheduleAlert   : (AlertType, Long, Long, String, String) -> Unit,
    onTabSelected     : (AlertTab) -> Unit,
    onFabClicked      : () -> Unit,
    onDismissSheet    : () -> Unit,
    onDismissPermDialog: () -> Unit
) {
    val context    = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = modifier.fillMaxSize().statusBarsPadding()) {

        Column(modifier = Modifier.fillMaxSize()) {
            AlertTabRow(
                selectedTab   = selectedTab,
                onTabSelected = onTabSelected
            )

            when (selectedTab) {
                AlertTab.ACTIVE -> ActiveAlertsContent(
                    weatherAlertsState = weatherAlertsState,
                    scheduledAlerts    = scheduledAlerts,
                    onCancelScheduled  = onCancelAlert,
                    onRetry            = onRetry
                )
                AlertTab.HISTORY -> EmptyStateContent(
                    message = "Your past alerts will appear here once\nyou've set up and triggered alerts."
                )
            }
        }

        AddAlertFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick  = onFabClicked
        )
    }

    if (showPermDialog) {
        AlarmPermissionDialog(
            onDismiss     = onDismissPermDialog,
            onOpenSettings = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    context.startActivity(
                        android.content.Intent(
                            android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        )
                    )
            }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = onDismissSheet,
            sheetState       = sheetState,
            containerColor   = Color(0xFFF0F6FF),
            shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle       = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 4.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFBBDEFB))
                )
            }
        ) {
            AddAlertSheetContent(
                onDismiss = onDismissSheet,
                onSave    = { type, sH, sM, eH, eM, sLbl, eLbl ->
                    onScheduleAlert(
                        type,
                        AlertScheduler.toEpochMillis(sH, sM),
                        AlertScheduler.toEpochMillis(eH, eM),
                        sLbl,
                        eLbl
                    )
                }
            )
        }
    }
}



@Composable
private fun ActiveAlertsContent(
    weatherAlertsState: UiState<List<WeatherAlert>>,
    scheduledAlerts   : List<AlertItem>,
    onCancelScheduled : (AlertItem) -> Unit,
    onRetry           : () -> Unit
) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize(),
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(Modifier.height(4.dp))
            SectionHeader("Scheduled Alerts", Color(0xFF1E88E5))
        }

        if (scheduledAlerts.isEmpty()) {
            item {
                InfoCard(
                    icon     = Icons.Default.Home,
                    color    = Color(0xFF90A4AE),
                    title    = "No scheduled alerts",
                    subtitle = "Tap + to add one"
                )
            }
        } else {
            items(scheduledAlerts, key = { it.id }) { item ->
                ScheduledAlertCard(item = item, onCancel = { onCancelScheduled(item) })
            }
        }
    }
}


@Composable
private fun TimeChip(label: String, time: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFE0B2)) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFFBF6B00), fontSize = 9.sp))
            Text(time, style = MaterialTheme.typography.labelSmall.copy(
                color = Color(0xFF7B3900), fontWeight = FontWeight.Bold, fontSize = 11.sp))
        }
    }
}



@Composable
private fun ScheduledAlertCard(item: AlertItem, onCancel: () -> Unit) {
    Surface(
        shape           = RoundedCornerShape(16.dp),
        color           = Color.White.copy(alpha = 0.85f),
        shadowElevation = 4.dp,
        modifier        = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(item.type.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.type.icon, null, tint = item.type.color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.type.label, style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold, color = Color(0xFF0D2B4E)))
                Text(item.label, style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF5B7FA6), fontSize = 12.sp))
            }
            IconButton(onClick = onCancel) {
                Icon(Icons.Default.Delete, "Cancel alert", tint = Color(0xFFE53935))
            }
        }
    }
}

@Composable
private fun InfoCard(icon: ImageVector, color: Color, title: String, subtitle: String) {
    Surface(shape = RoundedCornerShape(14.dp), color = Color.White.copy(alpha = 0.7f)) {
        Row(
            modifier          = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold, color = Color(0xFF0D2B4E)))
                Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF5B7FA6), fontSize = 12.sp))
            }
        }
    }
}


@Composable
private fun EmptyStateContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                Icon(Icons.Default.Notifications, null,
                    modifier = Modifier.size(44.dp), tint = Color(0xFF42A5F5))
            }
            Text(
                text      = message,
                style     = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF8FA4B8), fontSize = 14.sp, lineHeight = 21.sp
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun AddAlertSheetContent(
    onDismiss: () -> Unit,
    onSave   : (AlertType, Int, Int, Int, Int, String, String) -> Unit
) {
    val context     = LocalContext.current
    var selType     by remember { mutableStateOf(AlertType.NOTIFICATION) }
    var startHour   by remember { mutableStateOf(-1) }
    var startMinute by remember { mutableStateOf(-1) }
    var endHour     by remember { mutableStateOf(-1) }
    var endMinute   by remember { mutableStateOf(-1) }
    var startLabel  by remember { mutableStateOf("") }
    var endLabel    by remember { mutableStateOf("") }
    var startError  by remember { mutableStateOf(false) }
    var endError    by remember { mutableStateOf(false) }

    fun showPicker(onPicked: (Int, Int, String) -> Unit) {
        val cal = Calendar.getInstance()
        TimePickerDialog(context, { _, h, m ->
            val amPm = if (h < 12) "AM" else "PM"
            val hr   = if (h % 12 == 0) 12 else h % 12
            onPicked(h, m, "$hr:${m.toString().padStart(2, '0')} $amPm")
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "New Weather Alert",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF0D2B4E)
            )
        )
        Text(
            "Alert Type",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF5B7FA6), fontWeight = FontWeight.SemiBold, fontSize = 12.sp
            )
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AlertType.entries.forEach { type ->
                AlertTypeCard(
                    type       = type,
                    isSelected = selType == type,
                    onClick    = { selType = type },
                    modifier   = Modifier.weight(1f)
                )
            }
        }
        Text(
            "Time Range",
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF5B7FA6), fontWeight = FontWeight.SemiBold, fontSize = 12.sp
            )
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TimePickerField(
                label    = "Start Time", value = startLabel, hasError = startError,
                onClick  = {
                    showPicker { h, m, l ->
                        startHour = h; startMinute = m; startLabel = l; startError = false
                    }
                },
                modifier = Modifier.weight(1f)
            )
            TimePickerField(
                label    = "End Time", value = endLabel, hasError = endError,
                onClick  = {
                    showPicker { h, m, l ->
                        endHour = h; endMinute = m; endLabel = l; endError = false
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick  = onDismiss,
                modifier = Modifier.weight(1f).height(50.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF1E88E5)),
                border   = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF1E88E5))
            ) { Text("Cancel", fontWeight = FontWeight.SemiBold) }

            Button(
                onClick = {
                    startError = startHour == -1
                    endError   = endHour   == -1
                    if (!startError && !endError)
                        onSave(selType, startHour, startMinute, endHour, endMinute, startLabel, endLabel)
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5))
            ) { Text("Save Alert", fontWeight = FontWeight.SemiBold, color = Color.White) }
        }
    }
}

@Composable
private fun AlertTypeCard(
    type: AlertType, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) type.color else Color(0xFFD9E8F7),
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        color = if (isSelected) type.color.copy(alpha = 0.12f) else Color.White,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(type.icon, null,
                tint     = if (isSelected) type.color else Color(0xFF90A4AE),
                modifier = Modifier.size(26.dp))
            Text(type.label, style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize   = 13.sp,
                color      = if (isSelected) Color(0xFF0D2B4E) else Color(0xFF90A4AE)))
            Text(type.subtitle, style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.sp, color = Color(0xFF90A4AE)))
        }
    }
}

@Composable
private fun TimePickerField(
    label: String, value: String, hasError: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    val borderColor = when {
        hasError           -> Color(0xFFE53935)
        value.isNotBlank() -> Color(0xFF1E88E5)
        else               -> Color(0xFFD9E8F7)
    }
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (value.isNotBlank() || hasError) 2.dp else 1.dp,
                color = borderColor, shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        color = if (value.isNotBlank()) Color(0xFFE3F2FD) else Color.White,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(label, style = MaterialTheme.typography.bodySmall.copy(
                color      = if (hasError) Color(0xFFE53935) else Color(0xFF5B7FA6),
                fontSize   = 11.sp, fontWeight = FontWeight.Medium))
            Text(
                text      = value.ifBlank { "--:-- --" },
                style     = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    color      = if (value.isBlank()) Color(0xFFBBDEFB) else Color(0xFF0D2B4E)
                ),
                textAlign = TextAlign.Center
            )
            if (hasError) Text("Required", style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFFE53935), fontSize = 10.sp))
        }
    }
}


@Composable
private fun AlertTabRow(selectedTab: AlertTab, onTabSelected: (AlertTab) -> Unit) {
    val tabs = AlertTab.entries
    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        containerColor   = Color.Transparent,
        contentColor     = Color.White,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)])
                    .height(3.dp)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(Color.White)
            )
        },
        divider = {
            HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
        }
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            val iconAlpha  by animateFloatAsState(
                targetValue   = if (isSelected) 1f else 0.45f,
                animationSpec = tween(200), label = "alpha"
            )
            Tab(selected = isSelected, onClick = { onTabSelected(tab) }, modifier = Modifier.height(52.dp)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(tab.icon, null, Modifier.size(18.dp).alpha(iconAlpha),
                        tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f))
                    Text(tab.label, style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize   = 14.sp),
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f))
                }
            }
        }
    }
}

@Composable
private fun AddAlertFab(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        onClick        = onClick,
        modifier       = modifier,
        containerColor = Color(0xFF1E88E5),
        contentColor   = Color.White,
        shape          = CircleShape,
        elevation      = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
    ) {
        Icon(Icons.Filled.Add, "Add Alert", Modifier.size(26.dp))
    }
}
