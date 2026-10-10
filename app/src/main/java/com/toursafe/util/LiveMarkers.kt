package com.toursafe.util

import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.MapMarker
import com.toursafe.data.models.MarkerType
import com.toursafe.data.models.SafeAccommodation

/**
 * Turns live alerts and places into markers for the schematic canvas map.
 *
 * Honesty rules:
 *  - No data, no markers: returns an empty list (not even a "you" dot) so the screen shows its status panel.
 *  - Alerts too far away to fit on the canvas without being clamped to its edge are left off the map
 *    (they remain in the Alerts list); a clamped dot would show a wrong position.
 *  - isDemo / isVerified are carried over from the source records, never invented.
 */
object LiveMarkers {

    private const val MIN_EXTENT_KM = 2.0
    /** projectToCanvas maps `extent` to 40% of the canvas, so 1.25x extent is exactly the canvas edge. */
    private const val EDGE_FACTOR = 1.25

    fun build(
        userLat: Double,
        userLng: Double,
        userLabel: String,
        places: List<SafeAccommodation>,
        alerts: List<DisasterAlert>
    ): List<MapMarker> {
        if (places.isEmpty() && alerts.isEmpty()) return emptyList()

        val extentKm = maxOf(MIN_EXTENT_KM, places.maxOfOrNull { it.distanceKm } ?: 0.0)
        val out = mutableListOf<MapMarker>()

        out += MapMarker(
            id = "user",
            title = userLabel,
            type = MarkerType.USER_LOCATION,
            distanceKm = 0.0,
            description = GeoMath.formatCoordinates(userLat, userLng),
            normX = 0.5f,
            normY = 0.5f,
            latitude = userLat,
            longitude = userLng,
            isDemo = false,
            isVerified = true
        )

        for (place in places) {
            val (x, y) = GeoMath.projectToCanvas(userLat, userLng, place.latitude, place.longitude, extentKm)
            out += MapMarker(
                id = "place-${place.id}",
                title = place.name,
                type = when {
                    place.isEmergencyShelter -> MarkerType.EMERGENCY_SHELTER
                    !place.isDemo -> MarkerType.EMERGENCY_SERVICE
                    else -> MarkerType.SAFE_ACCOMMODATION
                },
                distanceKm = place.distanceKm,
                description = "${place.type} · ${place.address}",
                normX = x,
                normY = y,
                latitude = place.latitude,
                longitude = place.longitude,
                isDemo = place.isDemo,
                isVerified = place.isVerified
            )
        }

        for (alert in alerts) {
            if (alert.distanceKm > extentKm * EDGE_FACTOR) continue
            val (x, y) = GeoMath.projectToCanvas(userLat, userLng, alert.latitude, alert.longitude, extentKm)
            out += MapMarker(
                id = "alert-${alert.id}",
                title = alert.title,
                type = MarkerType.HAZARD_ZONE,
                distanceKm = alert.distanceKm,
                description = "${alert.disasterType} · ${alert.sourceLabel}",
                normX = x,
                normY = y,
                severityLevel = alert.severity.name,
                latitude = alert.latitude,
                longitude = alert.longitude,
                isDemo = alert.isSimulated,
                isVerified = alert.isVerified
            )
        }
        return out
    }
}
