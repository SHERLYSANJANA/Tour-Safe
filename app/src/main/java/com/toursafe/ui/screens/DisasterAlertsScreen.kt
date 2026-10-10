package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toursafe.R
import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.repository.LoadStatus
import com.toursafe.ui.components.AlertCard
import com.toursafe.ui.components.DataStatusPanel
import com.toursafe.ui.components.PartialDataNote
import com.toursafe.ui.components.SourceAttribution
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.util.LocationState
import com.toursafe.viewmodels.AlertsViewModel
import com.toursafe.viewmodels.GpsLocationViewModel
import com.toursafe.viewmodels.SettingsViewModel

@Composable
fun DisasterAlertsScreen(
    viewModel: AlertsViewModel,
    locationViewModel: GpsLocationViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    onAlertClick: (String) -> Unit
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

    var retryTick by remember { mutableStateOf(0) }

    LaunchedEffect(locationState, demoModeEnabled, retryTick) {
        val userLat = (locationState as? LocationState.Success)?.location?.latitude ?: 0.0
        val userLng = (locationState as? LocationState.Success)?.location?.longitude ?: 0.0
        viewModel.refreshData(userLat, userLng, demoModeEnabled)
    }

    val filteredAlerts by viewModel.filteredAlerts.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val allAlerts by viewModel.allAlerts.collectAsState()
    val status by viewModel.status.collectAsState()

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.active_disaster_alerts)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == null,
                        onClick = { viewModel.filterBySeverity(null) },
                        label = { Text(stringResource(R.string.view_all)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AlertSeverity.CRITICAL,
                        onClick = { viewModel.filterBySeverity(AlertSeverity.CRITICAL) },
                        label = { Text(stringResource(R.string.severity_critical)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.error,
                            selectedLabelColor = MaterialTheme.colorScheme.onError
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AlertSeverity.WARNING,
                        onClick = { viewModel.filterBySeverity(AlertSeverity.WARNING) },
                        label = { Text(stringResource(R.string.severity_warning)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.tourSafe.warning,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilter == AlertSeverity.INFO,
                        onClick = { viewModel.filterBySeverity(AlertSeverity.INFO) },
                        label = { Text(stringResource(R.string.severity_info)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            selectedLabelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }

            if (filteredAlerts.isEmpty()) {
                if (allAlerts.isNotEmpty()) {
                    // Alerts exist, but none match the selected severity filter.
                    Text(
                        text = stringResource(R.string.alerts_filter_no_match),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    DataStatusPanel(
                        status = status,
                        noneNearbyTitle = R.string.alerts_none_nearby_title,
                        noneNearbyDesc = R.string.alerts_none_nearby_desc,
                        onRetry = { retryTick++ }
                    )
                }
            } else {
                if (status == LoadStatus.PARTIAL) PartialDataNote()
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredAlerts) { alert ->
                        AlertCard(
                            alert = alert,
                            onClick = { onAlertClick(alert.id) }
                        )
                    }
                }
            }

            if (!demoModeEnabled && (status == LoadStatus.LOADED || status == LoadStatus.PARTIAL)) {
                SourceAttribution(R.string.alerts_sources_note)
            }
        }
    }
}
