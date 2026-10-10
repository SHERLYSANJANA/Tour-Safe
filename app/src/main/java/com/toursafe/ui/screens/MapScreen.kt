package com.toursafe.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.toursafe.R
import com.toursafe.data.mock.MockData
import com.toursafe.data.models.MapMarker
import com.toursafe.data.models.MarkerType
import com.toursafe.ui.components.InteractiveMapCanvas
import com.toursafe.ui.components.TourSafeTopBar
import com.toursafe.ui.theme.*

@Composable
fun MapScreen(
    onNavigateToShelters: () -> Unit
) {
    val markers = remember { MockData.mapMarkers }
    val hazardZones = remember { MockData.hazardZones }
    var selectedMarker by remember { mutableStateOf<MapMarker?>(markers.firstOrNull()) }

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

                        if (marker.type == MarkerType.SAFE_ACCOMMODATION || marker.type == MarkerType.EMERGENCY_SHELTER) {
                            Button(
                                onClick = onNavigateToShelters,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = AccentCyan),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.view_safe_accommodations),
                                    color = androidx.compose.ui.graphics.Color.Black,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(
    color: androidx.compose.ui.graphics.Color,
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
