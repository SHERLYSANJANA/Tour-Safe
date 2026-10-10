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
import com.toursafe.data.models.MapMarker
import com.toursafe.data.models.MarkerType
import com.toursafe.ui.components.InteractiveMapCanvas
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*
import com.toursafe.util.LocationState
import com.toursafe.util.LocationUtils
import com.toursafe.viewmodels.GpsLocationViewModel

@Composable
fun MapScreen(
    locationViewModel: GpsLocationViewModel = viewModel(),
    onNavigateToShelters: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        locationViewModel.initLocationManager(context)
        if (locationViewModel.hasPermission()) {
            locationViewModel.fetchLocation()
        }
    }

    val locationState by locationViewModel.locationState.collectAsState()
    var showPermissionExplanation by remember { mutableStateOf(false) }

    val userLat = (locationState as? LocationState.Success)?.location?.latitude ?: 0.0
    val userLng = (locationState as? LocationState.Success)?.location?.longitude ?: 0.0

    val markers = remember(userLat, userLng) { MockData.getAnchoredMapMarkers(userLat, userLng) }
    val hazardZones = remember(userLat, userLng) { MockData.getAnchoredHazardZones(userLat, userLng) }
    var selectedMarker by remember(markers) { mutableStateOf<MapMarker?>(markers.firstOrNull()) }

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
        containerColor = BackgroundNavy
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
                    .background(SurfaceNavy, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(color = AlertRed, label = stringResource(R.string.legend_hazard_zone))
                LegendDot(color = SafetyGreen, label = stringResource(R.string.legend_free_shelter))
                LegendDot(color = AccentCyan, label = stringResource(R.string.legend_safe_hotel))
                LegendDot(color = AccentBlue, label = stringResource(R.string.legend_you))
            }

            // Selected Marker Information Panel
            selectedMarker?.let { marker ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceNavy)
                        .border(1.dp, SurfaceVariantNavy, RoundedCornerShape(16.dp))
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
                                color = TextPrimary
                            )
                            Text(
                                text = stringResource(R.string.km_away, marker.distanceKm),
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentCyan
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = marker.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            if (marker.type == MarkerType.SAFE_ACCOMMODATION || marker.type == MarkerType.EMERGENCY_SHELTER) {
                                Button(
                                    onClick = onNavigateToShelters,
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = stringResource(R.string.view_safe_accommodations),
                                        color = Color.Black,
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
                                        destinationName = marker.title
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = SurfaceVariantNavy),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Directions, contentDescription = null, tint = TextPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = stringResource(R.string.directions),
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.labelLarge
                                )
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
                containerColor = SurfaceNavy,
                title = { Text(stringResource(R.string.location_permission_explanation_title), color = TextPrimary) },
                text = { Text(stringResource(R.string.location_permission_explanation_desc), color = TextSecondary) },
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
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan)
                    ) {
                        Text(stringResource(R.string.use_my_location), color = Color.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPermissionExplanation = false }) {
                        Text(stringResource(R.string.cancel), color = TextSecondary)
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
            .background(SurfaceNavy)
            .border(1.dp, SurfaceVariantNavy, RoundedCornerShape(16.dp))
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
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = AccentCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "GPS Location",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary
                        )
                    }
                    Button(
                        onClick = onUseLocationClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.use_my_location), color = Color.Black, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            is LocationState.Loading -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AccentCyan, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.gps_acquiring),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
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
                            Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null, tint = SafetyGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            BadgeChip(
                                text = if (loc.isApproximate) stringResource(R.string.approximate_gps_badge) else stringResource(R.string.precise_gps_badge),
                                bgColor = if (loc.isApproximate) WarningAmberBg else SafetyGreenBg,
                                textColor = if (loc.isApproximate) WarningAmber else SafetyGreen
                            )
                        }
                        IconButton(onClick = onRefreshClick) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh", tint = AccentCyan)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Lat: ${"%.4f".format(loc.latitude)}, Lng: ${"%.4f".format(loc.longitude)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary
                    )
                    Text(
                        text = stringResource(R.string.location_accuracy, loc.accuracyMeters),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            is LocationState.GpsDisabled -> {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = WarningAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.gps_disabled_title),
                            style = MaterialTheme.typography.titleSmall,
                            color = WarningAmber
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.gps_disabled_desc),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onOpenSettingsClick,
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.open_location_settings), color = Color.Black, style = MaterialTheme.typography.labelMedium)
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
                        color = AlertRed,
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = onRefreshClick,
                        colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(stringResource(R.string.refresh_location), color = Color.Black, style = MaterialTheme.typography.labelSmall)
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
            color = TextSecondary
        )
    }
}
