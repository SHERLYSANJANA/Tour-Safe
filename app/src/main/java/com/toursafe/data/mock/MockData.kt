package com.toursafe.data.mock

import com.toursafe.data.models.*
import com.toursafe.util.LocationUtils

object MockData {

    val languages = listOf(
        LanguageOption("en", "English", "English", "🇺🇸", isSelected = true),
        LanguageOption("es", "Spanish", "Español", "🇪🇸"),
        LanguageOption("fr", "French", "Français", "🇫🇷"),
        LanguageOption("de", "German", "Deutsch", "🇩🇪"),
        LanguageOption("zh", "Chinese", "中文", "🇨🇳"),
        LanguageOption("ja", "Japanese", "日本語", "🇯🇵"),
        LanguageOption("th", "Thai", "ไทย", "🇹🇭"),
        LanguageOption("hi", "Hindi", "हिन्दी", "🇮🇳")
    )

    fun getAnchoredAlerts(userLat: Double = 0.0, userLng: Double = 0.0): List<DisasterAlert> {
        val baseLat = if (userLat != 0.0) userLat else 7.8804
        val baseLng = if (userLng != 0.0) userLng else 98.3923

        val baseAlerts = listOf(
            DisasterAlert(
                id = "alert-001",
                title = "Coastal / High Surge Advisory",
                disasterType = "Coastal Hazard & High Waves",
                severity = AlertSeverity.CRITICAL,
                affectedArea = "Coastal & Low-lying Shoreline Areas",
                distanceKm = 0.0,
                latitude = baseLat + 0.018,
                longitude = baseLng - 0.012,
                timestamp = "10 mins ago • Local Time",
                sourceLabel = "[SIMULATED DISASTER ALERT] National Disaster Warning Center",
                description = "High surge warning issued for low-lying coastal areas. Residents and tourists are urged to move inland to elevated safe ground above 15 meters.",
                officialInstructions = listOf(
                    "Move inland and climb higher ground immediately.",
                    "Do not go near beaches or low-lying shorelines.",
                    "Follow designated emergency evacuation route signs.",
                    "Stay tuned to official emergency radio broadcasts."
                ),
                recommendedActions = listOf(
                    "Check-in on TourSafe as 'Safe' or 'Moving to Safety'.",
                    "Locate nearest safe certified shelter in Safe Places tab.",
                    "Keep emergency bag with essential document copies and water."
                )
            ),
            DisasterAlert(
                id = "alert-002",
                title = "Severe Flash Flood Watch",
                disasterType = "Heavy Rainfall & Landslide Hazard",
                severity = AlertSeverity.WARNING,
                affectedArea = "District Hillsides & Low-lying Valleys",
                distanceKm = 0.0,
                latitude = baseLat + 0.035,
                longitude = baseLng + 0.022,
                timestamp = "1 hour ago • Local Time",
                sourceLabel = "[SIMULATED DISASTER ALERT] Hydro Meteorological Dept",
                description = "Persistent heavy rainfall exceeding 120mm in hillside areas causes elevated risk of rapid flash flooding and mudslides across low-lying roads.",
                officialInstructions = listOf(
                    "Avoid driving across flooded roads or low water bridges.",
                    "Secure valuables above floor level if in hillside valleys.",
                    "Be prepared for potential short power outages."
                ),
                recommendedActions = listOf(
                    "Charge phone battery and power bank.",
                    "Check vehicle fuel level and park on high ground."
                )
            ),
            DisasterAlert(
                id = "alert-003",
                title = "Tropical Storm Wind Advisory",
                disasterType = "High Winds & Gale Warning",
                severity = AlertSeverity.ADVISORY,
                affectedArea = "Regional Coastal District",
                distanceKm = 0.0,
                latitude = baseLat - 0.042,
                longitude = baseLng - 0.028,
                timestamp = "3 hours ago • Local Time",
                sourceLabel = "[SIMULATED DISASTER ALERT] Regional Weather Center",
                description = "Sustained wind speeds up to 55 km/h with gusts up to 75 km/h expected through late evening.",
                officialInstructions = listOf(
                    "Secure loose outdoor objects and balcony items.",
                    "Avoid standing near tall trees or unsecured scaffolding."
                ),
                recommendedActions = listOf(
                    "Confirm local transit schedules before departing."
                )
            )
        )

        return baseAlerts.map { alert ->
            val dist = LocationUtils.calculateDistanceKm(baseLat, baseLng, alert.latitude, alert.longitude)
            alert.copy(distanceKm = if (dist < 0.1) 0.8 else dist)
        }.sortedBy { it.distanceKm }
    }

    fun getAnchoredAccommodations(userLat: Double = 0.0, userLng: Double = 0.0): List<SafeAccommodation> {
        val baseLat = if (userLat != 0.0) userLat else 7.8804
        val baseLng = if (userLng != 0.0) userLng else 98.3923

        val baseAccs = listOf(
            SafeAccommodation(
                id = "acc-101",
                name = "Safe Haven Evacuation Resort",
                type = "Certified Disaster Evacuation Partner",
                address = "Highland Safety Zone, District Center",
                distanceKm = 0.0,
                latitude = baseLat + 0.008,
                longitude = baseLng + 0.006,
                pricePerNight = "$45 / night",
                rating = 4.9,
                reviewCount = 128,
                capacityAvailable = 18,
                maxCapacity = 120,
                isEmergencyShelter = true,
                emergencyAmenities = listOf("Backup Diesel Generator", "24/7 First Aid Staff", "Clean Water Purification", "Concrete Earthquake Structure"),
                contactPhone = "+1 800 555 0199"
            ),
            SafeAccommodation(
                id = "acc-102",
                name = "Central Civic Free Shelter",
                type = "Government Designated Free Shelter",
                address = "Municipal Sports Complex & Safety Hub",
                distanceKm = 0.0,
                latitude = baseLat - 0.012,
                longitude = baseLng + 0.010,
                pricePerNight = "FREE Emergency Shelter",
                rating = 4.7,
                reviewCount = 210,
                capacityAvailable = 150,
                maxCapacity = 500,
                isEmergencyShelter = true,
                emergencyAmenities = listOf("Red Cross Medical Triage", "Free Emergency Meals", "Multilingual Interpreters", "Child Safety Zone"),
                contactPhone = "1784 (Emergency Hotline)"
            ),
            SafeAccommodation(
                id = "acc-103",
                name = "Highland Hilltop Eco Hotel",
                type = "High-Elevation Safe Hotel",
                address = "High Elevation Ridge Road",
                distanceKm = 0.0,
                latitude = baseLat + 0.022,
                longitude = baseLng - 0.015,
                pricePerNight = "$65 / night",
                rating = 4.8,
                reviewCount = 94,
                capacityAvailable = 8,
                maxCapacity = 80,
                isEmergencyShelter = false,
                emergencyAmenities = listOf("High Altitude Elevation (85m above sea level)", "Solar Backup Power", "Satellite Internet", "Emergency Rations"),
                contactPhone = "+1 800 555 0288"
            )
        )

        return baseAccs.map { acc ->
            val dist = LocationUtils.calculateDistanceKm(baseLat, baseLng, acc.latitude, acc.longitude)
            acc.copy(distanceKm = if (dist < 0.1) 0.9 else dist)
        }.sortedBy { it.distanceKm }
    }

    fun getAnchoredMapMarkers(userLat: Double = 0.0, userLng: Double = 0.0): List<MapMarker> {
        val baseLat = if (userLat != 0.0) userLat else 7.8804
        val baseLng = if (userLng != 0.0) userLng else 98.3923

        val accs = getAnchoredAccommodations(baseLat, baseLng)
        val alerts = getAnchoredAlerts(baseLat, baseLng)

        val markers = mutableListOf<MapMarker>()

        // 1. User Marker at center
        markers.add(
            MapMarker(
                id = "m-user",
                title = "Your Real GPS Location",
                type = MarkerType.USER_LOCATION,
                distanceKm = 0.0,
                description = "Lat: ${"%.4f".format(baseLat)}, Lng: ${"%.4f".format(baseLng)}",
                normX = 0.50f,
                normY = 0.50f,
                latitude = baseLat,
                longitude = baseLng
            )
        )

        // 2. Map Accommodations around user
        accs.forEach { acc ->
            val dLat = acc.latitude - baseLat
            val dLng = acc.longitude - baseLng
            val normX = (0.5 + (dLng * 12.0)).coerceIn(0.12, 0.88).toFloat()
            val normY = (0.5 - (dLat * 12.0)).coerceIn(0.12, 0.88).toFloat()

            markers.add(
                MapMarker(
                    id = acc.id,
                    title = acc.name,
                    type = if (acc.isEmergencyShelter) MarkerType.EMERGENCY_SHELTER else MarkerType.SAFE_ACCOMMODATION,
                    distanceKm = acc.distanceKm,
                    description = "${acc.type} • ${acc.distanceKm} km away",
                    normX = normX,
                    normY = normY,
                    latitude = acc.latitude,
                    longitude = acc.longitude
                )
            )
        }

        // 3. Map Hazard Alert Marker
        alerts.firstOrNull()?.let { alert ->
            val dLat = alert.latitude - baseLat
            val dLng = alert.longitude - baseLng
            val normX = (0.5 + (dLng * 12.0)).coerceIn(0.12, 0.88).toFloat()
            val normY = (0.5 - (dLat * 12.0)).coerceIn(0.12, 0.88).toFloat()

            markers.add(
                MapMarker(
                    id = alert.id,
                    title = alert.title,
                    type = MarkerType.HAZARD_ZONE,
                    distanceKm = alert.distanceKm,
                    description = "${alert.affectedArea} (${alert.distanceKm} km)",
                    normX = normX,
                    normY = normY,
                    latitude = alert.latitude,
                    longitude = alert.longitude
                )
            )
        }

        return markers
    }

    fun getAnchoredHazardZones(userLat: Double = 0.0, userLng: Double = 0.0): List<HazardZone> {
        val baseLat = if (userLat != 0.0) userLat else 7.8804
        val baseLng = if (userLng != 0.0) userLng else 98.3923

        return listOf(
            HazardZone(
                id = "hz-1",
                title = "Coastal Hazard Zone",
                riskType = "Tsunami / Coastal Surge Risk",
                normCenterX = 0.32f,
                normCenterY = 0.38f,
                radiusNormalized = 0.16f,
                latitude = baseLat + 0.018,
                longitude = baseLng - 0.012
            )
        )
    }

    val alerts = getAnchoredAlerts()
    val accommodations = getAnchoredAccommodations()
    val mapMarkers = getAnchoredMapMarkers()
    val hazardZones = getAnchoredHazardZones()

    val propertyReviews = listOf(
        PropertyReview(
            id = "rev-1",
            accommodationId = "acc-101",
            userName = "Sarah Jenkins",
            userCountry = "Australia",
            rating = 5.0,
            date = "Yesterday",
            comment = "During the sudden coastal advisory, the staff was incredible! They instantly alerted all guests and had clear evacuation maps."
        ),
        PropertyReview(
            id = "rev-2",
            accommodationId = "acc-101",
            userName = "Marco Rossi",
            userCountry = "Italy",
            rating = 4.8,
            date = "3 days ago",
            comment = "Very sturdy building, high elevation, and great backup power during severe weather."
        ),
        PropertyReview(
            id = "rev-3",
            accommodationId = "acc-102",
            userName = "Kenji Sato",
            userCountry = "Japan",
            rating = 5.0,
            date = "1 week ago",
            comment = "An ideal safe spot away from surge zones. Extremely clean and professional staff."
        )
    )

    val initialEmergencyContacts = listOf(
        EmergencyContact("c-1", "Emma Watson", "Spouse / Family", "+1 (555) 019-2834", isPrimary = true),
        EmergencyContact("c-2", "David Miller", "Travel Partner", "+1 (555) 234-5678", isPrimary = false),
        EmergencyContact("c-3", "Embassy Emergency Line", "Official Embassy", "+1 (800) 555-4000", isPrimary = false)
    )

    val initialNotifications = listOf(
        "SIMULATED: Coastal Advisory issued for nearby region - 10 mins ago",
        "SIMULATED: Emergency Shelter opened at Municipal Center - 45 mins ago",
        "System: TourSafe downloaded offline safety guides - 2 hours ago",
        "System: Language preference set - Yesterday"
    )
}
