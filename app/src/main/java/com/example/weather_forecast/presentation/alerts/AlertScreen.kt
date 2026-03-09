package com.example.weather_forecast.presentation.alerts

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class AlertTab(val label: String, val icon: ImageVector) {
    ACTIVE("Active", Icons.Default.Notifications),
    HISTORY("History", Icons.Default.Home)
}

@Composable
fun AlertScreen(modifier: Modifier = Modifier) {

    var selectedTab by remember { mutableStateOf(AlertTab.ACTIVE) }

    Box(modifier = modifier
        .fillMaxSize()
        .statusBarsPadding()
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            AlertTabRow(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            AnimatedVisibility(
                visible = true,
                enter = fadeIn(tween(300)) + slideInVertically(tween(400)) { it / 4 },
                exit = fadeOut()
            ) {
                EmptyStateContent(tab = selectedTab)
            }
        }


        AddAlertFab(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        )
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
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[tabs.indexOf(selectedTab)])
                    .height(3.dp)
                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        },
        divider = {
            HorizontalDivider(color = Color.White.copy(alpha = 0.3f), thickness = 1.dp)
        }
    ) {
        tabs.forEach { tab ->
            val isSelected = tab == selectedTab
            val iconAlpha by animateFloatAsState(
                targetValue = if (isSelected) 1f else 0.45f,
                animationSpec = tween(200),
                label = "iconAlpha"
            )

            Tab(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(18.dp)
                            .alpha(iconAlpha),
                        tint = if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                    )
                    Text(
                        text = tab.label,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 14.sp
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
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(horizontal = 40.dp)
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
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(44.dp),
                    tint = Color(0xFF42A5F5)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (isActive) "No Active Alerts" else "No Alert History",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1A2B4A)
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isActive)
                    "You haven't set up any weather alerts yet.\nTap + to create your first alert."
                else
                    "Your past alerts will appear here once\nyou've set up and triggered alerts.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF8FA4B8),
                    fontSize = 14.sp,
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
                        text = "Tap  +  below to add an alert",
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFF1E88E5),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AddAlertFab(modifier: Modifier = Modifier) {
    FloatingActionButton(
        onClick = {
            // TODO: navigate to add-alert screen
        },
        modifier = modifier,
        containerColor = Color(0xFF1E88E5),
        contentColor = Color.White,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add Alert",
            modifier = Modifier.size(26.dp)
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