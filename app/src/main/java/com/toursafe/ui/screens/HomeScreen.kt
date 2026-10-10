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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toursafe.R
import com.toursafe.navigation.Screen
import com.toursafe.ui.components.AlertCard
import com.toursafe.ui.components.StatusBanner
import com.toursafe.ui.theme.*
import com.toursafe.util.LocationState
import com.toursafe.viewmodels.GpsLocationViewModel
import com.toursafe.viewmodels.HomeViewModel
import com.toursafe.viewmodels.SettingsViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
    locationViewModel: GpsLocationViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    onNavigate: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        settingsViewModel.loadSettings(context)
        locationViewModel.initLocationManager(context)
        if (locationViewModel.hasPermission()) {
            locationViewModel.fetchLocation()
        }
    }

    val locationState by locationViewModel.locationState.collectAsState()
    val demoModeEnabled by settingsViewModel.demoModeEnabled.collectAsState()

    LaunchedEffect(locationState, demoModeEnabled) {
        val userLat = (locationState as? LocationState.Success)?.location?.latitude ?: 0.0
        val userLng = (locationState as? LocationState.Success)?.location?.longitude ?: 0.0
        viewModel.refreshData(userLat, userLng, demoModeEnabled, context)
    }

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
                            text = stringResource(R.string.dashboard_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = TextPrimary
                        )
                        Text(
                            text = status.locationName,
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
                    text = stringResource(R.string.quick_actions_header),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.emergency_sos),
                            subtitle = stringResource(R.string.sos_subtitle),
                            icon = Icons.Default.Sos,
                            bgColor = AlertRedBg,
                            borderColor = AlertRed,
                            iconTint = AlertRed,
                            onClick = { onNavigate(Screen.EmergencySos.route) }
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            title = stringResource(R.string.voice_sos_tab),
                            subtitle = stringResource(R.string.start_voice_sos),
                            icon = Icons.Default.Mic,
                            bgColor = SurfaceNavy,
                            borderColor = AccentCyan,
                            iconTint = AccentCyan,
                            onClick = { onNavigate(Screen.EmergencySos.route) }
                        )
                    }
                    QuickActionButton(
                        modifier = Modifier.fillMaxWidth(),
                        title = stringResource(R.string.safe_places),
                        subtitle = stringResource(R.string.shelters_hotels_subtitle),
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
                        text = stringResource(R.string.active_disaster_alerts),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    TextTextButton(
                        text = "${stringResource(R.string.view_all)} (${alerts.size})",
                        onClick = { onNavigate(Screen.Alerts.route) }
                    )
                }
            }

            if (alerts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(SurfaceNavy, RoundedCornerShape(16.dp))
                            .border(1.dp, SurfaceVariantNavy, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.no_verified_source_title),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.no_verified_source_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(alerts) { alert ->
                    AlertCard(
                        alert = alert,
                        onClick = { onNavigate(Screen.AlertDetails.createRoute(alert.id)) }
                    )
                }
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
