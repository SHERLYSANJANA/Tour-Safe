package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.toursafe.navigation.Screen
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackClick: () -> Unit,
    onNavigateToLanguage: () -> Unit
) {
    val pushEnabled by viewModel.pushNotificationsEnabled.collectAsState()
    val disasterAlertsEnabled by viewModel.disasterAlertsEnabled.collectAsState()
    val locationPermissionMock by viewModel.locationPermissionMock.collectAsState()

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = "App Settings",
                showBackButton = true,
                onBackClick = onBackClick
            )
        },
        containerColor = BackgroundNavy
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Preferences & Regional",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            // Language Selection Item
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceNavy, RoundedCornerShape(14.dp))
                    .clickable { onNavigateToLanguage() }
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = AccentCyan
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Change App Language",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = TextSecondary
                    )
                }
            }

            Text(
                text = "Notifications & Permissions",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )

            // Toggle 1: Push Notifications
            SwitchSettingRow(
                title = "Push Notifications",
                subtitle = "Receive updates on general app safety advice",
                checked = pushEnabled,
                onCheckedChange = { viewModel.togglePushNotifications(it) }
            )

            // Toggle 2: High Priority Disaster Alerts
            SwitchSettingRow(
                title = "High Priority Disaster Alerts",
                subtitle = "Override silent mode for critical tsunami or weather alerts",
                checked = disasterAlertsEnabled,
                onCheckedChange = { viewModel.toggleDisasterAlerts(it) }
            )

            // Toggle 3: Location Access
            SwitchSettingRow(
                title = "GPS Location Permission (Simulated)",
                subtitle = "Allow TourSafe to provide localized hazard map warnings",
                checked = locationPermissionMock,
                onCheckedChange = { viewModel.toggleLocationPermissionMock(it) }
            )
        }
    }
}

@Composable
private fun SwitchSettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceNavy, RoundedCornerShape(14.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    checkedTrackColor = AccentCyan
                )
            )
        }
    }
}
