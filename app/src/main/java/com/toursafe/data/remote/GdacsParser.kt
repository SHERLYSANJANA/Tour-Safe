package com.toursafe.data.remote

import com.toursafe.data.models.AlertSeverity
import com.toursafe.data.models.DisasterAlert
import com.toursafe.util.GeoMath
import com.toursafe.util.TimeText
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * GDACS (UN OCHA / European Commission) event list. Used for cyclones, floods, volcanoes,
 * wildfires and tsunamis. Earthquakes are skipped here because USGS already covers them.
 *
 * Parsing is deliberately defensive: any missing field is skipped rather than crashing.
 */
object GdacsParser {

    const val FEED_URL = "https://www.gdacs.org/gdacsapi/api/events/geteventlist/EVENTS4APP"
    const val SOURCE_LABEL = "GDACS (UN / European Commission)"

    private data class TypeInfo(val label: String, val radiusKm: Double)

    private val types = mapOf(
        "TC" to TypeInfo("Tropical Cyclone", 1000.0),
        "FL" to TypeInfo("Flood", 500.0),
        "VO" to TypeInfo("Volcano", 500.0),
        "WF" to TypeInfo("Wildfire", 300.0),
        "TS" to TypeInfo("Tsunami", 1000.0)
    )

    fun parse(
        json: String,
        userLat: Double,
        userLng: Double,
        nowMs: Long = System.currentTimeMillis()
    ): List<DisasterAlert> {
        val features = JSONObject(json).optJSONArray("features") ?: return emptyList()
        val seen = HashSet<String>()
        val out = mutableListOf<DisasterAlert>()

        for (i in 0 until features.length()) {
            val f = features.optJSONObject(i) ?: continue
            val props = f.optJSONObject("properties") ?: continue
            val geometry = f.optJSONObject("geometry") ?: continue
            if (geometry.optString("type") != "Point") continue
            val coords = geometry.optJSONArray("coordinates") ?: continue
            if (coords.length() < 2) continue

            val typeInfo = types[props.optString("eventtype")] ?: continue
            if (props.optString("iscurrent").equals("false", ignoreCase = true)) continue

            val severity = when (props.optString("alertlevel").lowercase(Locale.ROOT)) {
                "red" -> AlertSeverity.CRITICAL
                "orange" -> AlertSeverity.WARNING
                else -> continue // Green = low humanitarian impact; skip to avoid noise
            }

            val lng = coords.optDouble(0)
            val lat = coords.optDouble(1)
            if (lat.isNaN() || lng.isNaN()) continue
            val distanceKm = GeoMath.distanceKm(userLat, userLng, lat, lng)
            if (distanceKm > typeInfo.radiusKm) continue

            val eventId = props.optString("eventid", "")
            val dedupeKey = "${props.optString("eventtype")}-$eventId"
            if (eventId.isNotBlank() && !seen.add(dedupeKey)) continue

            val country = props.optString("country", "")
            val name = props.optString("name", "").ifBlank { props.optString("eventname", "") }
            val title = name.ifBlank { "${typeInfo.label}${if (country.isNotBlank()) " – $country" else ""}" }
            val fromMs = parseDate(props.optString("fromdate", ""))
            val report = props.optJSONObject("url")?.optString("report", "").orEmpty()
            val severityText = props.optJSONObject("severitydata")?.optString("severitytext", "").orEmpty()
            val baseDescription = props.optString("description", "").ifBlank { title }

            out += DisasterAlert(
                id = "gdacs-${props.optString("eventtype")}-${eventId.ifBlank { i.toString() }}",
                title = title,
                disasterType = typeInfo.label,
                severity = severity,
                affectedArea = country.ifBlank { "See source for affected area" },
                distanceKm = GeoMath.roundTo1Decimal(distanceKm),
                timestamp = TimeText.relative(fromMs, nowMs),
                sourceLabel = SOURCE_LABEL,
                description = buildString {
                    append(baseDescription)
                    if (severityText.isNotBlank()) append(" ").append(severityText)
                    append(" Event center is about ${"%.0f".format(distanceKm)} km from your location.")
                },
                officialInstructions = if (report.isNotBlank()) listOf("Event report: $report") else emptyList(),
                recommendedActions = listOf(
                    "General guidance (not an official instruction): check local news and your government's travel advisory.",
                    "Keep your phone charged and your emergency contacts saved.",
                    "Follow instructions from local authorities."
                ),
                latitude = lat,
                longitude = lng,
                isSimulated = false,
                isVerified = true,
                issuedAtEpochMs = fromMs,
                expiresAtEpochMs = null
            )
        }
        return out
    }

    private fun parseDate(text: String): Long? {
        if (text.isBlank()) return null
        val patterns = listOf("yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss'Z'", "yyyy-MM-dd")
        for (p in patterns) {
            try {
                val fmt = SimpleDateFormat(p, Locale.US)
                fmt.timeZone = TimeZone.getTimeZone("UTC")
                return fmt.parse(text)?.time
            } catch (e: Exception) {
                // try next pattern
            }
        }
        return null
    }
}
