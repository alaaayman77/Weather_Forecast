package com.example.weather_forecast.presentation.alerts.components

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather_forecast.data.models.AlertType
import com.example.weather_forecast.data.models.CustomCondition
import com.example.weather_forecast.presentation.alerts.TimePickerField
import com.example.weather_forecast.ui.theme.lightGray
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
 fun CustomConditionTab(
    onDismiss: () -> Unit,
    onSave   : (AlertType, Int, Int, Int, Int, String, String, CustomCondition) -> Unit
) {
    val context        = LocalContext.current
    var selCondition   by remember { mutableStateOf<CustomCondition?>(null) }
    var selType        by remember { mutableStateOf(AlertType.NOTIFICATION) }
    var startHour      by remember { mutableStateOf(-1) }
    var startMinute    by remember { mutableStateOf(-1) }
    var endHour        by remember { mutableStateOf(-1) }
    var endMinute      by remember { mutableStateOf(-1) }
    var startLabel     by remember { mutableStateOf("") }
    var endLabel       by remember { mutableStateOf("") }
    var startError     by remember { mutableStateOf<String?>(null) }
    var endError       by remember { mutableStateOf<String?>(null) }
    var conditionError by remember { mutableStateOf(false) }

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

    val groups = listOf(
        "⛈️  Thunderstorm"   to listOf(CustomCondition.THUNDERSTORM),
        "🌧️  Rain & Drizzle" to listOf(
            CustomCondition.DRIZZLE,
            CustomCondition.RAIN_LIGHT,
            CustomCondition.RAIN_HEAVY,
            CustomCondition.RAIN_FREEZING
        ),
        "❄️  Snow & Sleet"   to listOf(
            CustomCondition.SNOW,
            CustomCondition.SLEET
        ),
        "🌫️  Atmosphere"     to listOf(
            CustomCondition.FOG_MIST,
            CustomCondition.DUST_SAND,
            CustomCondition.TORNADO_SQUALL
        ),
        "☀️  Clear & Clouds" to listOf(
            CustomCondition.CLEAR_SKY,
            CustomCondition.PARTLY_CLOUDY,
            CustomCondition.CLOUDY
        ),
        "🌡️  Temperature"    to listOf(
            CustomCondition.HIGH_TEMP,
            CustomCondition.LOW_TEMP
        )
    )

    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Watch for condition",
            style = MaterialTheme.typography.labelMedium.copy(color = lightGray)
        )
        if (conditionError) {
            Text(
                "Please select a condition",
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFFE53935))
            )
        }

        Column(
            modifier            = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            groups.forEach { (groupTitle, conditions) ->
                Text(
                    groupTitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color      = lightGray,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                FlowRow(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement   = Arrangement.spacedBy(8.dp)
                ) {
                    conditions.forEach { condition ->
                        val isSelected = selCondition == condition
                        Surface(
                            shape    = RoundedCornerShape(20.dp),
                            color    = if (isSelected)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                            else Color.White,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else Color(0xFFD9E8F7),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    selCondition   = condition
                                    conditionError = false
                                }
                        ) {
                            Text(
                                text     = "${condition.emoji} ${condition.label}",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                style    = MaterialTheme.typography.labelMedium.copy(
                                    color      = if (isSelected)
                                        MaterialTheme.colorScheme.primary
                                    else Color(0xFF607D8B),
                                    fontWeight = if (isSelected) FontWeight.Bold
                                    else FontWeight.Normal,
                                    fontSize   = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Text("Alert Type", style = MaterialTheme.typography.labelMedium.copy(color = lightGray))
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
            "Check within time range",
            style = MaterialTheme.typography.labelMedium.copy(color = lightGray)
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            TimePickerField(
                label    = "From", value = startLabel, errorMsg = startError,
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
                label    = "Until", value = endLabel, errorMsg = endError,
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
            ) { Text("Cancel", fontWeight = FontWeight.SemiBold) }

            Button(
                onClick = {
                    if (selCondition == null) conditionError = true
                    if (startHour    == -1)   startError     = "Required"
                    if (endHour      == -1)   endError       = "Required"
                    if (selCondition != null && startError == null &&
                        endError == null && startHour != -1 && endHour != -1
                    ) {
                        onSave(
                            selType, startHour, startMinute,
                            endHour, endMinute, startLabel, endLabel,
                            selCondition!!
                        )
                    }
                },
                modifier = Modifier.weight(1f).height(50.dp),
                shape    = RoundedCornerShape(14.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) { Text("Save Alert", fontWeight = FontWeight.SemiBold, color = Color.White) }
        }
    }
}
