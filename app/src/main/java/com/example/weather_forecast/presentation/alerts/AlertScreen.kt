package com.example.weather_forecast.presentation.alerts

import android.app.TimePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Warning
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.presentation.alerts.components.AlertTypeCard
import java.util.Calendar


private enum class AlertTab(val label: String, val icon: ImageVector) {
    ACTIVE("Active", Icons.Default.Notifications),
    HISTORY("History", Icons.Default.Home)
}

 enum class AlertType(
    val label: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color
) {
    NOTIFICATION(
        label    = "Notification",
        subtitle = "Silent push alert",
        icon     = Icons.Default.Notifications,
        color    = Color(0xFF42A5F5)
    ),
    ALERT(
        label    = "Alert",
        subtitle = "Sound + vibration",
        icon     = Icons.Outlined.Warning,
        color    = Color(0xFFFF7043)
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertScreen(modifier: Modifier = Modifier) {

    var selectedTab      by remember { mutableStateOf(AlertTab.ACTIVE) }
    var showBottomSheet  by remember { mutableStateOf(false) }
    val sheetState       = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            AlertTabRow(
                selectedTab   = selectedTab,
                onTabSelected = { selectedTab = it }
            )
            AnimatedVisibility(
                visible = true,
                enter   = fadeIn(tween(300)) + slideInVertically(tween(400)) { it / 4 },
                exit    = fadeOut()
            ) {
                EmptyStateContent(tab = selectedTab)
            }
        }

        AddAlertFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            onClick  = { showBottomSheet = true }
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest  = { showBottomSheet = false },
            sheetState        = sheetState,
            containerColor    = Color(0xFFF0F6FF),
            shape             = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            dragHandle        = {
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
                onDismiss = { showBottomSheet = false },
                onSave    = { showBottomSheet = false }
            )
        }
    }
}


@Composable
private fun AddAlertSheetContent(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    val context         = LocalContext.current
    var selectedType    by remember { mutableStateOf(AlertType.NOTIFICATION) }
    var startTime       by remember { mutableStateOf("") }
    var endTime         by remember { mutableStateOf("") }
    var startError      by remember { mutableStateOf(false) }
    var endError        by remember { mutableStateOf(false) }

    fun showTimePicker(onTimeSelected: (String) -> Unit) {
        val cal = Calendar.getInstance()
        TimePickerDialog(
            context,
            { _, hour, minute ->
                val amPm  = if (hour < 12) "AM" else "PM"
                val h     = if (hour % 12 == 0) 12 else hour % 12
                val m     = minute.toString().padStart(2, '0')
                onTimeSelected("$h:$m $amPm")
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            false
        ).show()
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
            text  = "New Weather Alert",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize   = 20.sp,
                color      = Color(0xFF0D2B4E)
            )
        )

        Text(
            text  = "Alert Type",
            style = MaterialTheme.typography.labelMedium.copy(
                color      = Color(0xFF5B7FA6),
                fontWeight = FontWeight.SemiBold,
                fontSize   = 12.sp
            )
        )
        Row(
            modifier            = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AlertType.entries.forEach { type ->
                AlertTypeCard(
                    type       = type,
                    isSelected = selectedType == type,
                    onClick    = { selectedType = type },
                    modifier   = Modifier.weight(1f)
                )
            }
        }

        Text(
            text  = "Time Range",
            style = MaterialTheme.typography.labelMedium.copy(
                color      = Color(0xFF5B7FA6),
                fontWeight = FontWeight.SemiBold,
                fontSize   = 12.sp
            )
        )
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TimePickerField(
                label    = "Start Time",
                value    = startTime,
                hasError = startError,
                onClick  = {
                    showTimePicker { t ->
                        startTime  = t
                        startError = false
                    }
                },
                modifier = Modifier.weight(1f)
            )
            TimePickerField(
                label    = "End Time",
                value    = endTime,
                hasError = endError,
                onClick  = {
                    showTimePicker { t ->
                        endTime  = t
                        endError = false
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF1E88E5)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.5.dp,
                    color = Color(0xFF1E88E5)
                )
            ) {
                Text(
                    text  = "Cancel",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            //save btn
            Button(
                onClick = {
                    startError = startTime.isBlank()
                    endError   = endTime.isBlank()
                    if (!startError && !endError) onSave()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E88E5)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text  = "Save Alert",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White
                    )
                )
            }
        }
    }
}





@Composable
private fun TimePickerField(
    label: String,
    value: String,
    hasError: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = when {
        hasError   -> Color(0xFFE53935)
        value.isNotBlank() -> Color(0xFF1E88E5)
        else       -> Color(0xFFD9E8F7)
    }
    val bgColor = if (value.isNotBlank()) Color(0xFFE3F2FD) else Color.White

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (value.isNotBlank() || hasError) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick),
        color = bgColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 14.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text  = label,
                style = MaterialTheme.typography.bodySmall.copy(
                    color    = if (hasError) Color(0xFFE53935) else Color(0xFF5B7FA6),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text  = value.ifBlank { "--:-- --" },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 18.sp,
                    color      = if (value.isBlank()) Color(0xFFBBDEFB)
                    else Color(0xFF0D2B4E)
                ),
                textAlign = TextAlign.Center
            )
            if (hasError) {
                Text(
                    text  = "Required",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color    = Color(0xFFE53935),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}


@Composable
private fun AlertTabRow(
    selectedTab: AlertTab,
    onTabSelected: (AlertTab) -> Unit
) {
    val tabs = AlertTab.entries

    TabRow(
        selectedTabIndex = tabs.indexOf(selectedTab),
        containerColor   = Color.Transparent,
        contentColor     = Color.White,
        indicator        = { tabPositions ->
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
            val iconAlpha by animateFloatAsState(
                targetValue    = if (isSelected) 1f else 0.45f,
                animationSpec  = tween(200),
                label          = "iconAlpha"
            )
            Tab(
                selected = isSelected,
                onClick  = { onTabSelected(tab) },
                modifier = Modifier.height(52.dp)
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector        = tab.icon,
                        contentDescription = null,
                        modifier           = Modifier.size(18.dp).alpha(iconAlpha),
                        tint               = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                    )
                    Text(
                        text  = tab.label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize   = 14.sp
                        ),
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                    )
                }
            }
        }
    }
}


@Composable
private fun EmptyStateContent(tab: AlertTab) {
    val isActive = tab == AlertTab.ACTIVE
    Box(
        modifier        = Modifier.fillMaxSize(),
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
                        Brush.radialGradient(
                            colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier           = Modifier.size(44.dp),
                    tint               = Color(0xFF42A5F5)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = if (isActive) "No Active Alerts" else "No Alert History",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize   = 20.sp,
                    color      = Color(0xFF1A2B4A)
                ),
                textAlign = TextAlign.Center
            )
            Text(
                text  = if (isActive)
                    "You haven't set up any weather alerts yet.\nTap + to create your first alert."
                else
                    "Your past alerts will appear here once\nyou've set up and triggered alerts.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color      = Color(0xFF8FA4B8),
                    fontSize   = 14.sp,
                    lineHeight = 21.sp
                ),
                textAlign = TextAlign.Center
            )
            if (isActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFFE3F2FD)
                ) {
                    Text(
                        text     = "Tap  +  below to add an alert",
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        style    = MaterialTheme.typography.labelMedium.copy(
                            color      = Color(0xFF1E88E5),
                            fontWeight = FontWeight.Medium,
                            fontSize   = 12.sp
                        )
                    )
                }
            }
        }
    }
}


@Composable
private fun AddAlertFab(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick        = onClick,
        modifier       = modifier,
        containerColor = Color(0xFF1E88E5),
        contentColor   = Color.White,
        shape          = CircleShape,
        elevation      = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Icon(
            imageVector        = Icons.Filled.Add,
            contentDescription = "Add Alert",
            modifier           = Modifier.size(26.dp)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AlertScreenPreview() {
    MaterialTheme {
        AlertScreen()
    }
}