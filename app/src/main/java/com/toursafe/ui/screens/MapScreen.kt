package com.toursafe.ui.screens

import android.Manifest
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.toursafe.R
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.HazardZone
import com.toursafe.data.models.MapMarker
import com.toursafe.data.models.MarkerType
import com.toursafe.data.repository.LoadStatus
import com.toursafe.data.repository.combineStatus
import com.toursafe.ui.components.DataStatusPanel
import com.toursafe.ui.components.InteractiveMapCanvas
import com.toursafe.ui.components.SourceAttribution
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.util.LocationState
import com.toursafe.util.LiveMarkers
import com.toursafe.util.LocationUtils
import com.toursafe.viewmodels.AccommodationViewModel
import com.toursafe.viewmodels.AlertsViewModel
import com.toursafe.viewmodels.GpsLocationViewModel
import com.toursafe.viewmodels.SettingsViewModel

@Composable
fun MapScreen(
    locationViewModel: GpsLocationViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel(),
    alertsViewModel: AlertsViewModel = viewModel(),
    accommodationViewModel: AccommodationViewModel = viewModel(),
    onNavigateToShelters: () -> Unit
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
    var showPermissionExplanation by remember { mutableStateOf(false) }

    val userLat = (locationState as? LocationState.Success)?.location?.latitude ?: 0.0
    val userLng = (locationState as? LocationState.Success)?.location?.longitude ?: 0.0
    var retryTick by remember { mutableStateOf(0) }

    LaunchedEffect(locationState, demoModeEnabled, retryTick) {
        alertsViewModel.refreshData(userLat, userLng, demoModeEnabled)
        accommodationViewModel.refreshData(userLat, userLng, demoModeEnabled)
    }

    val livePlaces by accommodationViewModel.accommodations.collectAsState()
    val liveAlerts by alertsViewModel.allAlerts.collectAsState()
    val placesStatus by accommodationViewModel.status.collectAsState()
    val alertsStatus by alertsViewModel.status.collectAsState()
    val mapStatus = combineStatus(placesStatus, alertsStatus)
    val youLabel = stringResource(R.string.legend_you)

    val markers = remember(demoModeEnabled, livePlaces, liveAlerts, userLat, userLng, youLabel) {
        if (demoModeEnabled) {
            MockData.demoMapMarkers
        } else if (userLat == 0.0 && userLng == 0.0) {
            emptyList()
        } else {
            LiveMarkers.build(userLat, userLng, youLabel, livePlaces, liveAlerts)
        }
    }
    val hazardZones = remember(demoModeEnabled) {
        if (demoModeEnabled) listOf(
            HazardZone("hz-1", "Coastal Hazard Zone (Demo)", "Demo Tsunami Risk Zone", 0.32f, 0.38f, 0.16f, latitude = 7.8920, longitude = 98.2980, isDemo = true)
        ) else emptyList()
    }
    var selectedMarker by remember(markers) {
        mutableStateOf<MapMarker?>(markers.firstOrNull { it.type != MarkerType.USER_LOCATION } ?: markers.firstOrNull())
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            locationViewModel.fetchLocation()
        }
    }

    Scaffold(
        topBar = {
            TourSafeTopBar(
                title = stringResource(R.string.interactive_safety_map)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Real GPS Location Control Panel
            GpsControlPanel(
                locationState = locationState,
                onUseLocationClick = {
                    if (locationViewModel.hasPermission()) {
                        locationViewModel.fetchLocation()
                    } else {
                        showPermissionExplanation = true
                    }
                },
                onRefreshClick = { locationViewModel.refreshLocation() },
                onOpenSettingsClick = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    } catch (e: Exception) {}
                }
            )

            if (markers.isEmpty()) {
                DataStatusPanel(
                    status = mapStatus,
                    noneNearbyTitle = R.string.places_none_nearby_title,
                    noneNearbyDesc = R.string.places_none_nearby_desc,
                    onRetry = { retryTick++ }
                )
            } else {
                // Interactive Custom Canvas Map
                InteractiveMapCanvas(
                    markers = markers,
                    hazardZones = hazardZones,
                    selectedMarker = selectedMarker,
                    onMarkerSelect = { selectedMarker = it }
                )

                // Map Legend Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LegendDot(color = MaterialTheme.colorScheme.error, label = stringResource(R.string.legend_hazard_zone))
                    LegendDot(color = MaterialTheme.tourSafe.safe, label = stringResource(R.string.legend_free_shelter))
                    LegendDot(color = MaterialTheme.tourSafe.service, label = stringResource(R.string.legend_emergency_service))
                    LegendDot(color = MaterialTheme.colorScheme.primary, label = stringResource(R.string.legend_safe_hotel))
                    LegendDot(color = MaterialTheme.colorScheme.secondary, label = stringResource(R.string.legend_you))
                }

                if (!demoModeEnabled) {
                    SourceAttribution(R.string.osm_attribution)
                }

                // Selected Marker Information Panel
                selectedMarker?.let { marker ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = marker.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = stringResource(R.string.km_away, marker.distanceKm),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = marker.description,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                if (marker.type == MarkerType.SAFE_ACCOMMODATION || marker.type == MarkerType.EMERGENCY_SHELTER || marker.type == MarkerType.EMERGENCY_SERVICE) {
                                    Button(
                                        onClick = onNavigateToShelters,
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.view_safe_accommodations),
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    }
                                }

                                Button(
                                    onClick = {
                                        LocationUtils.openMapsDirections(
                                            context = context,
                                            destinationLat = marker.latitude,
                                            destinationLng = marker.longitude,
                                            destinationName = marker.title,
                                            isDemoRecord = marker.isDemo
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Directions, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = stringResource(R.string.directions),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Location Permission Pre-Explanation Dialog
        if (showPermissionExplanation) {
            AlertDialog(
                onDismissRequest = { showPermissionExplanation = false },
                containerColor = MaterialTheme.colorScheme.surface,
                title = { Text(stringResource(R.string.location_permission_explanation_title), color = MaterialTheme.colorScheme.onSurface) },
                text = { Text(stringResource(R.string.location_permission_explanation_desc), color = MaterialTheme.colorScheme.onSurfaceVariant) },
                confirmButton = {
                    Button(
                        onClick = {
                            showPermissionExplanation = false
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text(stringResource(R.string.use_my_location), color = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionExplanation = false }) {
                        Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            )
        }
    }
}

@Composable
private fun GpsControlPanel(
    locationState: LocationState,
    onUseLocationClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onOpenSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        when (locationState) {
            is LocationState.Idle, is LocationState.PermissionDenied -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GPS Location",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Button(
                        onClick = onUseLocationClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.use_my_location), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            is LocationState.Loading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.gps_acquiring),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is LocationState.Success -> {
                val loc = locationState.location
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, tint = MaterialTheme.tourSafe.safe)
                            Spacer(modifier = Modifier.width(8.dp))
                            BadgeChip(
                                text = if (loc.isApproximate) stringResource(R.string.approximate_gps_badge) else stringResource(R.string.precise_gps_badge),
                                bgColor = if (loc.isApproximate) MaterialTheme.tourSafe.warningContainer else MaterialTheme.tourSafe.safeContainer,
                                textColor = if (loc.isApproximate) MaterialTheme.tourSafe.warning else MaterialTheme.tourSafe.safe
                            )
                        }
                        IconButton(onClick = onRefreshClick) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = stringResource(R.string.refresh_location), tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Lat: ${"%.4f".format(loc.latitude)}, Lng: ${"%.4f".format(loc.longitude)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = stringResource(R.string.location_accuracy, loc.accuracyMeters),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(
                            R.string.location_updated,
                            android.text.format.DateUtils.getRelativeTimeSpanString(
                                loc.timestampMs,
                                System.currentTimeMillis(),
                                android.text.format.DateUtils.MINUTE_IN_MILLIS
                            ).toString()
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            is LocationState.GpsDisabled -> {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.tourSafe.warning)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.gps_disabled_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.tourSafe.warning
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.gps_disabled_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onOpenSettingsClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.tourSafe.warning),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.open_location_settings), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            is LocationState.Error -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = locationState.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = onRefreshClick,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.refresh_location), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun BadgeChip(
    text: String,
    bgColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = textColor, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LegendDot(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
