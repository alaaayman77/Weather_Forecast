package com.example.weather_forecast.presentation.alerts.view

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.AlertEntity
import com.example.weather_forecast.data.models.AlertMode
import com.example.weather_forecast.data.models.AlertStatus
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.data.models.CustomCondition
import com.example.weather_forecast.presentation.alerts.view.components.ActiveAlertsContent
import com.example.weather_forecast.presentation.alerts.view.components.AlarmPermissionDialog
import com.example.weather_forecast.presentation.alerts.view.components.AlertTypeCard
import com.example.weather_forecast.presentation.alerts.view.components.CustomConditionTab
import com.example.weather_forecast.presentation.alerts.view.components.NotificationPermissionDialog
import com.example.weather_forecast.presentation.AlertState
import com.example.weather_forecast.presentation.UiState
import com.example.weather_forecast.ui.theme.lightGray
import java.util.*
import com.example.weather_forecast.R
import com.example.weather_forecast.presentation.alerts.AlertScheduler

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AlertScreen(
    modifier                 : Modifier = Modifier,
    weatherAlertsState       : UiState<AlertState>,
    scheduledAlerts          : List<AlertEntity>,
    alertStatuses            : Map<Int, AlertStatus>,
    showBottomSheet          : Boolean,
    showPermDialog           : Boolean,
    canScheduleExact         : Boolean,
    onRetry                  : () -> Unit,
    onCancelAlert            : (AlertEntity) -> Unit,
    onScheduleAlert          : (AlertType, Long, Long, String, String, AlertMode, CustomCondition?) -> Unit,
    onFabClicked             : () -> Unit,
    onDismissSheet           : () -> Unit,
    onDismissPermDialog      : () -> Unit,
    showNotifPermDialog      : Boolean,
    onDismissNotifPermDialog : () -> Unit,
    onOpenNotifSettings      : () -> Unit,
) {
    val context    = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(modifier = modifier.fillMaxSize().statusBarsPadding()) {
        ActiveAlertsContent(
            weatherAlertsState = weatherAlertsState,
            scheduledAlerts    = scheduledAlerts,
            alertStatuses      = alertStatuses,
            onCancelScheduled  = onCancelAlert,
            onRetry            = onRetry,
        )
        AddAlertFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick  = onFabClicked
        )
    }

    if (showPermDialog) {
        AlarmPermissionDialog(
            onDismiss      = onDismissPermDialog,
            onOpenSettings = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    context.startActivity(
                        Intent(
                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                        )
                    )
            }
        )
    }

    if (showNotifPermDialog) {
        NotificationPermissionDialog(
            onDismiss      = onDismissNotifPermDialog,
            onOpenSettings = onOpenNotifSettings
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
                        .width(40.dp).height(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFBBDEFB))
                )
            }
        ) {
            AddAlertSheetContent(
                onDismiss = onDismissSheet,
                onSave    = { type, sH, sM, eH, eM, sL, eL, mode, condition ->
                    onScheduleAlert(
                        type,
                        AlertScheduler.Companion.toEpochMillis(sH, sM),
                        AlertScheduler.Companion.toEpochMillis(eH, eM),
                        sL, eL, mode, condition
                    )
                    onDismissSheet()
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddAlertSheetContent(
    onDismiss: () -> Unit,
    onSave   : (AlertType, Int, Int, Int, Int, String, String, AlertMode, CustomCondition?) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Column(
        modifier            = Modifier.fillMaxWidth().navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Text(
            stringResource(R.string.new_weather_alert),
            style    = MaterialTheme.typography.labelLarge.copy(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp

            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )

        TabRow(
            selectedTabIndex = selectedTab,
            containerColor   = Color.Transparent,
            contentColor     = MaterialTheme.colorScheme.primary,
            modifier         = Modifier.padding(horizontal = 20.dp)
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick  = { selectedTab = 0 },
                text     = {
                    Text(
                        stringResource(R.string.scheduled),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selectedTab == 0) FontWeight.Bold
                            else FontWeight.Normal
                        )
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick  = { selectedTab = 1 },
                text     = {
                    Text(
                        stringResource(R.string.custom_condition),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selectedTab == 1) FontWeight.Bold
                            else FontWeight.Normal
                        )
                    )
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        when (selectedTab) {
            0 -> ScheduledAlertTab(onDismiss = onDismiss) { type, sH, sM, eH, eM, sL, eL ->
                onSave(type, sH, sM, eH, eM, sL, eL, AlertMode.SCHEDULED, null)
            }
            1 -> CustomConditionTab(onDismiss = onDismiss) { type, sH, sM, eH, eM, sL, eL, cond ->
                onSave(type, sH, sM, eH, eM, sL, eL, AlertMode.CUSTOM, cond)
            }
        }
    }
}

@Composable
private fun ScheduledAlertTab(
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
    var startError  by remember { mutableStateOf<String?>(null) }
    var endError    by remember { mutableStateOf<String?>(null) }

    fun isInThePast(h: Int, m: Int): Boolean {
        val cal = Calendar.getInstance()
        return h < cal.get(Calendar.HOUR_OF_DAY) ||
                (h == cal.get(Calendar.HOUR_OF_DAY) && m <= cal.get(Calendar.MINUTE))
    }

    fun endNotAfterStart(sH: Int, sM: Int, eH: Int, eM: Int) =
        eH < sH || (eH == sH && eM <= sM)

    fun showPicker(onPicked: (Int, Int, String) -> Unit) {
        val cal = Calendar.getInstance()
        TimePickerDialog(context, { _, h, m ->
            val amPm = if (h < 12) "AM" else "PM"
            val hr   = if (h % 12 == 0) 12 else h % 12
            onPicked(h, m, "$hr:${m.toString().padStart(2, '0')} $amPm")
        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show()
    }

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.alert_type), style = MaterialTheme.typography.labelMedium.copy(color = lightGray))
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

        Text(stringResource(R.string.time_range), style = MaterialTheme.typography.labelMedium.copy(color = lightGray))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TimePickerField(
                label    = stringResource(R.string.start_time), value = startLabel, errorMsg = startError,
                onClick  = {
                    showPicker { h, m, l ->
                        if (isInThePast(h, m)) {
                            startError = "Can't be in the past"
                        } else {
                            startHour = h; startMinute = m; startLabel = l; startError = null
                            if (endHour != -1 && endNotAfterStart(h, m, endHour, endMinute))
                                endError = "Must be after start"
                            else if (endHour != -1) endError = null
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
            TimePickerField(
                label    = stringResource(R.string.end_time), value = endLabel, errorMsg = endError,
                onClick  = {
                    showPicker { h, m, l ->
                        when {
                            isInThePast(h, m) -> endError = "Can't be in the past"
                            startHour != -1 && endNotAfterStart(startHour, startMinute, h, m) ->
                                endError = "Must be after start"
                            else -> { endHour = h; endMinute = m; endLabel = l; endError = null }
                        }
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
                colors   = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border   = BorderStroke(1.5.dp, Color.Red)
            ) { Text(stringResource(R.string.cancel), fontWeight = FontWeight.SemiBold) }

            Button(
                onClick = {
                    if (startHour == -1) startError = "Required"
                    if (endHour   == -1) endError   = "Required"
                    if (startError == null && endError == null &&
                        startHour != -1 && endHour != -1
                    ) onSave(selType, startHour, startMinute, endHour, endMinute, startLabel, endLabel)
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) { Text(stringResource(R.string.save_alert), fontWeight = FontWeight.SemiBold, color = Color.White) }
        }
    }
}


@Composable
internal fun TimePickerField(
    label   : String,
    value   : String,
    errorMsg: String?,
    onClick : () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasError    = errorMsg != null
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
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        color = if (value.isNotBlank() && !hasError) Color(0xFFE3F2FD) else Color.White,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color      = if (hasError) Color(0xFFE53935) else Color(0xFF5B7FA6),
                    fontSize   = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text      = value.ifBlank { "--:-- --" },
                style     = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = if (value.isBlank()) Color(0xFFBBDEFB) else Color(0xFF0D2B4E)
                ),
                textAlign = TextAlign.Center
            )
            if (hasError) Text(
                errorMsg!!,
                style = MaterialTheme.typography.bodySmall.copy(
                    color    = Color(0xFFE53935),
                    fontSize = 10.sp
                )
            )
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