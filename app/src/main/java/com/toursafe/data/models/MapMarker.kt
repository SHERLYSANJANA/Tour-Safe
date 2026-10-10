package com.toursafe.data.models

enum class MarkerType {
    HAZARD_ZONE,
    EMERGENCY_SHELTER,
    SAFE_ACCOMMODATION,
    USER_LOCATION
}

data class MapMarker(
    val id: String,
    val title: String,
    val type: MarkerType,
    val distanceKm: Double,
    val description: String,
    val normX: Float, // Normalized offset X on canvas (0.0 .. 1.0)
    val normY: Float, // Normalized offset Y on canvas (0.0 .. 1.0)
    val severityLevel: String = "Medium"
)

data class HazardZone(
    val id: String,
    val title: String,
    val riskType: String, // e.g. "Coastal Flood Risk", "Landslide Hazard"
    val normCenterX: Float,
    val normCenterY: Float,
    val radiusNormalized: Float
)
