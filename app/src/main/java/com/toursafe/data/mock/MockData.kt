package com.toursafe.data.mock

import com.toursafe.data.models.*

object MockData {

    val languages = listOf(
        LanguageOption("en", "English", "English", "🇺🇸", isSelected = true),
        LanguageOption("es", "Spanish", "Español", "Español"),
        LanguageOption("fr", "French", "Français", "Français"),
        LanguageOption("de", "German", "Deutsch", "Deutsch"),
        LanguageOption("zh", "Chinese", "中文(简体)", "中文(简体)"),
        LanguageOption("ja", "Japanese", "日本語", "日本語"),
        LanguageOption("th", "Thai", "ไทย", "ไทย"),
        LanguageOption("hi", "Hindi", "हिन्दी", "हिन्दी")
    )

    val demoAlerts = listOf(
        DisasterAlert(
            id = "demo-alert-001",
            title = "Coastal / High Surge Advisory (Demo)",
            disasterType = "Coastal Hazard & High Waves",
            severity = AlertSeverity.CRITICAL,
            affectedArea = "Demo Coastal Zone",
            distanceKm = 1.2,
            latitude = 7.8920,
            longitude = 98.2980,
            timestamp = "10 mins ago",
            sourceLabel = "[DEMO] Simulated Disaster Advisory",
            description = "This is a simulated demo alert for testing UI. No real emergency exists.",
            officialInstructions = listOf(
                "Demo Instruction: Follow local authority guidance.",
                "Demo Instruction: Keep emergency contacts saved."
            ),
            recommendedActions = listOf(
                "Demo Action: Check-in status in app.",
                "Demo Action: Review nearby shelter routes."
            ),
            isSimulated = true,
            isVerified = false,
            issuedAtEpochMs = System.currentTimeMillis() - 600000L,
            expiresAtEpochMs = System.currentTimeMillis() + 86400000L
        ),
        DisasterAlert(
            id = "demo-alert-002",
            title = "Severe Flash Flood Watch (Demo)",
            disasterType = "Heavy Rainfall Watch",
            severity = AlertSeverity.WARNING,
            affectedArea = "Demo Hillside Slopes",
            distanceKm = 3.5,
            latitude = 7.9250,
            longitude = 98.3310,
            timestamp = "1 hour ago",
            sourceLabel = "[DEMO] Simulated Weather Dept",
            description = "Persistent rainfall in demo region. Unverified simulated data.",
            officialInstructions = listOf(
                "Demo Instruction: Avoid flooded roads."
            ),
            recommendedActions = listOf(
                "Demo Action: Charge phone battery."
            ),
            isSimulated = true,
            isVerified = false,
            issuedAtEpochMs = System.currentTimeMillis() - 3600000L,
            expiresAtEpochMs = System.currentTimeMillis() + 43200000L
        )
    )

    val demoAccommodations = listOf(
        SafeAccommodation(
            id = "demo-acc-101",
            name = "Demo Safe Evacuation Resort",
            type = "Demo Emergency Evacuation Partner",
            address = "123 Demo Safety Avenue",
            distanceKm = 1.2,
            latitude = 7.8842,
            longitude = 98.3891,
            pricePerNight = null,
            rating = null,
            reviewCount = 0,
            capacityAvailable = null,
            maxCapacity = null,
            isEmergencyShelter = true,
            emergencyAmenities = listOf("Backup Diesel Generator", "First Aid Staff"),
            contactPhone = null,
            isDemo = true,
            isVerified = false
        ),
        SafeAccommodation(
            id = "demo-acc-102",
            name = "Demo Central Civic Shelter",
            type = "Demo Municipal Free Shelter",
            address = "Municipal Center (Demo)",
            distanceKm = 2.5,
            latitude = 7.8885,
            longitude = 98.3970,
            pricePerNight = null,
            rating = null,
            reviewCount = 0,
            capacityAvailable = null,
            maxCapacity = null,
            isEmergencyShelter = true,
            emergencyAmenities = listOf("Medical Triage", "Emergency Meals"),
            contactPhone = null,
            isDemo = true,
            isVerified = false
        )
    )

    val demoMapMarkers = listOf(
        MapMarker("m-demo-1", "Demo Location", MarkerType.USER_LOCATION, 0.0, "Demo coordinates", 0.50f, 0.50f, latitude = 7.8804, longitude = 98.3923, isDemo = true, isVerified = false),
        MapMarker("m-demo-2", "Demo Safe Evacuation Resort", MarkerType.EMERGENCY_SHELTER, 1.2, "Demo shelter record", 0.58f, 0.52f, latitude = 7.8842, longitude = 98.3891, isDemo = true, isVerified = false)
    )

    val propertyReviews = listOf(
        PropertyReview(
            id = "rev-1",
            accommodationId = "demo-acc-101",
            userName = "Verified Traveler",
            userCountry = "Australia",
            rating = 5.0,
            date = "Yesterday",
            comment = "Demo review comment."
        )
    )

    val initialNotifications = listOf(
        "System: TourSafe initialized"
    )
}
