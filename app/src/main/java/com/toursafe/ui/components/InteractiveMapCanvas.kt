package com.toursafe.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.toursafe.data.models.HazardZone
import com.toursafe.data.models.MapMarker
import com.toursafe.data.models.MarkerType
import com.toursafe.ui.theme.*

@Composable
fun InteractiveMapCanvas(
    markers: List<MapMarker>,
    hazardZones: List<HazardZone>,
    selectedMarker: MapMarker?,
    onMarkerSelect: (MapMarker) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF0D1B2A))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(markers) {
                    detectTapGestures { tapOffset ->
                        val w = size.width
                        val h = size.height
                        // Find closest marker
                        val clickedMarker = markers.minByOrNull { marker ->
                            val mx = marker.normX * w
                            val my = marker.normY * h
                            val dx = tapOffset.x - mx
                            val dy = tapOffset.y - my
                            dx * dx + dy * dy
                        }
                        clickedMarker?.let { onMarkerSelect(it) }
                    }
                }
        ) {
            val w = size.width
            val h = size.height

            // 1. Draw Grid Lines simulating map roads/coastal grid
            val gridColor = Color(0xFF1E293B)
            for (i in 1..8) {
                drawLine(
                    color = gridColor,
                    start = Offset(i * w / 8, 0f),
                    end = Offset(i * w / 8, h),
                    strokeWidth = 1f
                )
                drawLine(
                    color = gridColor,
                    start = Offset(0f, i * h / 8),
                    end = Offset(w, i * h / 8),
                    strokeWidth = 1f
                )
            }

            // 2. Draw Simulated Coastal / Hazard Zones
            hazardZones.forEach { zone ->
                val cx = zone.normCenterX * w
                val cy = zone.normCenterY * h
                val r = zone.radiusNormalized * w

                drawCircle(
                    color = AlertRed.copy(alpha = 0.25f),
                    radius = r,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = AlertRed,
                    radius = r,
                    center = Offset(cx, cy),
                    style = Stroke(width = 2f)
                )
            }

            // 3. Draw Markers
            markers.forEach { marker ->
                val mx = marker.normX * w
                val my = marker.normY * h
                val isSelected = marker.id == selectedMarker?.id

                val markerColor = when (marker.type) {
                    MarkerType.USER_LOCATION -> AccentBlue
                    MarkerType.HAZARD_ZONE -> AlertRed
                    MarkerType.EMERGENCY_SHELTER -> SafetyGreen
                    MarkerType.SAFE_ACCOMMODATION -> AccentCyan
                }

                // Halo circle for selected
                if (isSelected) {
                    drawCircle(
                        color = markerColor.copy(alpha = 0.4f),
                        radius = 24f,
                        center = Offset(mx, my)
                    )
                }

                // Core marker point
                drawCircle(
                    color = markerColor,
                    radius = if (isSelected) 14f else 10f,
                    center = Offset(mx, my)
                )
            }
        }
    }
}
