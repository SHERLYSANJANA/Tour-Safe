package com.toursafe.data.remote

import com.toursafe.data.models.SafeAccommodation
import com.toursafe.util.GeoMath
import org.json.JSONObject
import java.util.Locale

/**
 * OpenStreetMap (Overpass API) emergency places: hospitals, police, fire stations,
 * assembly points and shelters. OSM is community-maintained, so every result is
 * returned as isVerified = false and is labelled as such in the UI.
 */
object OverpassParser {

    val ENDPOINTS = listOf(
        "https://overpass-api.de/api/interpreter",
        "https://overpass.kumi.systems/api/interpreter"
    )

    const val DEFAULT_RADIUS_M = 6000
    const val MAX_RESULTS = 40

    fun buildQuery(lat: Double, lng: Double, radiusM: Int = DEFAULT_RADIUS_M): String {
        val around = String.format(Locale.US, "(around:%d,%.5f,%.5f)", radiusM, lat, lng)
        return "[out:json][timeout:20];(" +
                "nwr[\"amenity\"~\"^(hospital|police|fire_station)\$\"]$around;" +
                "nwr[\"emergency\"=\"assembly_point\"]$around;" +
                "nwr[\"social_facility\"=\"shelter\"]$around;" +
                ");out tags center qt 150;"
    }

    fun parse(json: String, userLat: Double, userLng: Double): List<SafeAccommodation> {
        val elements = JSONObject(json).optJSONArray("elements") ?: return emptyList()
        val out = mutableListOf<SafeAccommodation>()

        for (i in 0 until elements.length()) {
            val el = elements.optJSONObject(i) ?: continue
            val tags = el.optJSONObject("tags") ?: continue

            val center = el.optJSONObject("center")
            val lat = if (el.has("lat")) el.optDouble("lat") else center?.optDouble("lat") ?: Double.NaN
            val lng = if (el.has("lon")) el.optDouble("lon") else center?.optDouble("lon") ?: Double.NaN
            if (lat.isNaN() || lng.isNaN()) continue

            val amenity = tags.optString("amenity")
            val isAssembly = tags.optString("emergency") == "assembly_point"
            val isShelter = tags.optString("social_facility") == "shelter"

            val kind = when {
                amenity == "hospital" -> "Hospital"
                amenity == "police" -> "Police Station"
                amenity == "fire_station" -> "Fire Station"
                isAssembly -> "Emergency Assembly Point"
                isShelter -> "Shelter"
                else -> continue
            }

            val name = tags.optString("name:en").ifBlank { tags.optString("name") }.ifBlank { kind }
            val street = listOf(tags.optString("addr:housenumber"), tags.optString("addr:street"))
                .filter { it.isNotBlank() }.joinToString(" ")
            val address = listOf(street, tags.optString("addr:city"))
                .filter { it.isNotBlank() }.joinToString(", ")
                .ifBlank { "Address not listed in OpenStreetMap" }
            val phone = tags.optString("phone").ifBlank { tags.optString("contact:phone") }.ifBlank { null }

            val amenities = mutableListOf<String>()
            if (amenity == "hospital" && tags.optString("emergency") == "yes") {
                amenities += "Emergency department (per OSM tag)"
            }

            out += SafeAccommodation(
                id = "osm-${el.optString("type", "x")}-${el.optLong("id", i.toLong())}",
                name = name,
                type = "$kind · OpenStreetMap",
                address = address,
                distanceKm = GeoMath.roundTo1Decimal(GeoMath.distanceKm(userLat, userLng, lat, lng)),
                isEmergencyShelter = isAssembly || isShelter,
                emergencyAmenities = amenities,
                contactPhone = phone,
                latitude = lat,
                longitude = lng,
                isDemo = false,
                isVerified = false
            )
        }
        return out.sortedBy { it.distanceKm }.take(MAX_RESULTS)
    }
}
