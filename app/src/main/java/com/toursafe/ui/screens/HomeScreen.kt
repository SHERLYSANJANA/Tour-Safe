package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.toursafe.navigation.Screen
import com.toursafe.ui.components.AlertCard
import com.toursafe.ui.components.StatusBanner
import com.toursafe.ui.theme.*
import com.toursafe.viewmodels.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigate: (String) -> Unit
) {
    val status by viewModel.userStatus.collectAsState()
    val alerts by viewModel.alerts.collectAsState()

    Scaffold(
        containerColor = BackgroundNavy
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TourSafe Dashboard",
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = "Location: ${status.locationName}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )
                    }

                    IconButton(
                        onClick = { onNavigate(Screen.NotificationHistory.route) },
                        modifier = Modifier.background(SurfaceNavy, RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = AccentCyan
                        )
                    }
                }
            }

            // Current Safety Status Card
            item {
                StatusBanner(
                    status = status,
                    onCheckInClick = { onNavigate(Screen.EmergencyCheckIn.route) }
                )
            }

            // Quick Actions Grid
            item {
                Text(
                    text = "Quick Safety Actions",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Emergency SOS",
                        subtitle = "Instant Signal",
                        icon = Icons.Default.Sos,
                        bgColor = AlertRedBg,
                        borderColor = AlertRed,
                        iconTint = AlertRed,
                        onClick = { onNavigate(Screen.EmergencySos.route) }
                    )
                    QuickActionButton(
                        modifier = Modifier.weight(1f),
                        title = "Safe Places",
                        subtitle = "Shelters & Hotels",
                        icon = Icons.Default.Hotel,
                        bgColor = SurfaceNavy,
                        borderColor = AccentCyan,
                        iconTint = AccentCyan,
                        onClick = { onNavigate(Screen.SafeAccommodation.route) }
                    )
                }
            }

            // Active Disaster Alerts Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Disaster Alerts",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    TextTextButton(
                        text = "View All (${alerts.size})",
                        onClick = { onNavigate(Screen.Alerts.route) }
                    )
                }
            }

            // Alerts List
            items(alerts) { alert ->
                AlertCard(
                    alert = alert,
                    onClick = { onNavigate(Screen.AlertDetails.createRoute(alert.id)) }
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    bgColor: Color,
    borderColor: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun TextTextButton(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = AccentCyan,
        modifier = Modifier.clickable { onClick() }
    )
}
