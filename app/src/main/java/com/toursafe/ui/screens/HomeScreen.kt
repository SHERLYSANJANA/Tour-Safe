package com.toursafe.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.toursafe.ui.components.EmergencySosButton
import com.toursafe.ui.components.QuickActionButton
import com.toursafe.ui.components.SectionHeader
import com.toursafe.navigation.Screen
import com.toursafe.ui.components.AlertCard
import com.toursafe.ui.components.DataStatusPanel
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
    val alertsStatus by viewModel.alertsStatus.collectAsState()

    // The bundled logo is black artwork; use the white variant on dark backgrounds.
    val isDarkBackground = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = Dimens.ScreenPadding, vertical = Dimens.SpaceMd),
            verticalArrangement = Arrangement.spacedBy(Dimens.SectionSpacing)
        ) {
            // Header: identity, current location, shortcuts
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                if (isDarkBackground) R.drawable.logo_toursafe_white else R.drawable.logo_toursafe
                            ),
                            contentDescription = stringResource(R.string.app_name),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.width(Dimens.ItemSpacing))
                        Column {
                            Text(
                                text = stringResource(R.string.dashboard_title),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = status.locationName,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )
                        }
                    }
                    IconButton(onClick = { onNavigate(Screen.NotificationHistory.route) }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = stringResource(R.string.notification_history_title),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onNavigate(Screen.Profile.route) }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = stringResource(R.string.nav_profile),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Current status + location
            item {
                StatusBanner(
                    status = status,
                    onCheckInClick = { onNavigate(Screen.EmergencyCheckIn.route) }
                )
            }

            // Emergency: one prominent button, and an honest note about what it does
            item {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.SpaceSm)) {
                    EmergencySosButton(onClick = { onNavigate(Screen.EmergencySos.route) })
                    Text(
                        text = stringResource(R.string.home_sos_explainer),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick actions (2 x 2)
            item {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing)) {
                    SectionHeader(title = stringResource(R.string.quick_actions_header))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.nav_map),
                            icon = Icons.Default.Map,
                            tint = MaterialTheme.colorScheme.primary,
                            onClick = { onNavigate(Screen.Map.route) }
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.safe_places),
                            icon = Icons.Default.Hotel,
                            tint = MaterialTheme.colorScheme.primary,
                            onClick = { onNavigate(Screen.SafeAccommodation.route) }
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.ItemSpacing)
                    ) {
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.nav_alerts),
                            icon = Icons.Default.Warning,
                            tint = MaterialTheme.tourSafe.warning,
                            onClick = { onNavigate(Screen.Alerts.route) }
                        )
                        QuickActionButton(
                            modifier = Modifier.weight(1f),
                            label = stringResource(R.string.check_in_screen_title),
                            icon = Icons.Default.CheckCircle,
                            tint = MaterialTheme.tourSafe.safe,
                            onClick = { onNavigate(Screen.EmergencyCheckIn.route) }
                        )
                    }
                }
            }

            // Alerts for the current area
            item {
                SectionHeader(
                    title = stringResource(R.string.active_disaster_alerts),
                    actionLabel = "${stringResource(R.string.view_all)} (${alerts.size})",
                    onAction = { onNavigate(Screen.Alerts.route) }
                )
            }

            if (alerts.isEmpty()) {
                item {
                    DataStatusPanel(
                        status = alertsStatus,
                        noneNearbyTitle = R.string.alerts_none_nearby_title,
                        noneNearbyDesc = R.string.alerts_none_nearby_desc
                    )
                }
            } else {
                items(alerts) { alert ->
                    AlertCard(
                        alert = alert,
                        onClick = { onNavigate(Screen.AlertDetails.createRoute(alert.id)) }
                    )
                }
            }

            // Compact, static travel tip (general advice, not a data claim)
            item {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(Dimens.SpaceMd),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(Dimens.IconMd)
                        )
                        Spacer(modifier = Modifier.width(Dimens.ItemSpacing))
                        Text(
                            text = stringResource(R.string.home_safety_tip),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
