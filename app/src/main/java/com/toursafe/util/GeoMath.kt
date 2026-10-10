package com.toursafe.util

import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

/** Pure-Kotlin geo helpers (no Android dependencies, so they are unit-testable). */
object GeoMath {

    private const val EARTH_RADIUS_KM = 6371.0088

    fun distanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        return 2 * EARTH_RADIUS_KM * asin(sqrt(a))
    }

    /** e.g. 7.8804° N, 98.3923° E  /  33.8688° S, 151.2093° E  /  40.7128° N, 74.0060° W */
    fun formatCoordinates(lat: Double, lng: Double): String {
        val ns = if (lat >= 0) "N" else "S"
        val ew = if (lng >= 0) "E" else "W"
        return String.format(java.util.Locale.US, "%.4f° %s, %.4f° %s", kotlin.math.abs(lat), ns, kotlin.math.abs(lng), ew)
    }

    fun roundTo1Decimal(value: Double): Double = (value * 10).roundToInt() / 10.0

    /**
     * Projects a point onto a 0..1 canvas with the user at (0.5, 0.5).
     * [maxExtentKm] is the distance that maps to 40% of the canvas width/height.
     */
    fun projectToCanvas(
        userLat: Double,
        userLng: Double,
        lat: Double,
        lng: Double,
        maxExtentKm: Double
    ): Pair<Float, Float> {
        val extent = if (maxExtentKm <= 0.0) 1.0 else maxExtentKm
        val eastKm = (lng - userLng) * 111.320 * cos(Math.toRadians(userLat))
        val northKm = (lat - userLat) * 110.574
        val x = (0.5 + (eastKm / extent) * 0.4).coerceIn(0.05, 0.95)
        val y = (0.5 - (northKm / extent) * 0.4).coerceIn(0.05, 0.95)
        return x.toFloat() to y.toFloat()
    }
}
