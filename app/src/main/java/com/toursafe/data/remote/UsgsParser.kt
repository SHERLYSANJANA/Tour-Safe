package com.toursafe.data.remote

import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import com.toursafe.util.GeoMath
import com.toursafe.util.TimeText
import org.json.JSONObject

/** USGS Earthquake Hazards Program GeoJSON feed (official, no API key). */
object UsgsParser {

    const val FEED_URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/4.5_day.geojson"
    const val SOURCE_LABEL = "USGS Earthquake Hazards Program"

    fun parse(
        json: String,
        userLat: Double,
        userLng: Double,
        radiusKm: Double = 1000.0,
        nowMs: Long = System.currentTimeMillis()
    ): List<DisasterAlert> {
        val features = JSONObject(json).optJSONArray("features") ?: return emptyList()
        val out = mutableListOf<DisasterAlert>()

        for (i in 0 until features.length()) {
            val f = features.optJSONObject(i) ?: continue
            val props = f.optJSONObject("properties") ?: continue
            val coords = f.optJSONObject("geometry")?.optJSONArray("coordinates") ?: continue
            if (coords.length() < 2 || props.isNull("mag")) continue

            val lng = coords.optDouble(0)
            val lat = coords.optDouble(1)
            if (lat.isNaN() || lng.isNaN()) continue

            val distanceKm = GeoMath.distanceKm(userLat, userLng, lat, lng)
            if (distanceKm > radiusKm) continue

            val mag = props.optDouble("mag")
            val depthKm = if (coords.length() > 2) coords.optDouble(2) else Double.NaN
            val place = props.optString("place", "Unknown location")
            val pager = props.optString("alert", "")
            val timeMs = if (props.isNull("time")) null else props.optLong("time")
            val tsunamiFlag = props.optInt("tsunami", 0) == 1
            val url = props.optString("url", "")

            val severity = when {
                mag >= 7.0 || pager == "red" || pager == "orange" -> AlertSeverity.CRITICAL
                mag >= 6.0 || pager == "yellow" -> AlertSeverity.WARNING
                else -> AlertSeverity.INFO
            }

            val depthText = if (depthKm.isNaN()) "" else ", depth ${"%.0f".format(depthKm)} km"
            val description = "Magnitude ${"%.1f".format(mag)} earthquake$depthText, " +
                    "about ${"%.0f".format(distanceKm)} km from your location. " +
                    "Reported by USGS; magnitude and location may be revised as more data arrives." +
                    if (tsunamiFlag) " This event occurred in an ocean region: check NOAA/local tsunami bulletins." else ""

            out += DisasterAlert(
                id = "usgs-${f.optString("id", i.toString())}",
                title = props.optString("title", "M${"%.1f".format(mag)} Earthquake"),
                disasterType = "Earthquake",
                severity = severity,
                affectedArea = place,
                distanceKm = GeoMath.roundTo1Decimal(distanceKm),
                timestamp = TimeText.relative(timeMs, nowMs),
                sourceLabel = SOURCE_LABEL,
                description = description,
                officialInstructions = if (url.isNotBlank()) listOf("Event details: $url") else emptyList(),
                recommendedActions = listOf(
                    "General guidance (not an official instruction): expect aftershocks; Drop, Cover and Hold On if shaking starts.",
                    "Move away from damaged buildings, coasts and steep slopes if you feel strong or long shaking.",
                    "Follow instructions from local authorities."
                ),
                latitude = lat,
                longitude = lng,
                isSimulated = false,
                isVerified = true,
                issuedAtEpochMs = timeMs,
                expiresAtEpochMs = null
            )
        }
        return out
    }
}
