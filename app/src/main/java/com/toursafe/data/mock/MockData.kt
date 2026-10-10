package com.toursafe.data.mock

import com.toursafe.data.models.*

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

    val alerts = listOf(
        DisasterAlert(
            id = "alert-001",
            title = "Coastal Tsunami Advisory",
            disasterType = "Tsunami / High Wave Warning",
            severity = AlertSeverity.CRITICAL,
            affectedArea = "Phuket West Coast beaches (Patong, Kata, Karon)",
            distanceKm = 3.2,
            timestamp = "10 mins ago • 14:32 Local Time",
            sourceLabel = "[SIMULATED DISASTER ALERT] National Disaster Warning Center",
            description = "Earthquake magnitude 7.2 registered off Sumatra coast. Coastal areas along Phuket shoreline are urged to evacuate immediately to elevated safe ground above 15 meters.",
            officialInstructions = listOf(
                "Move inland and climb higher ground immediately.",
                "Do not go near beaches to watch the waves.",
                "Follow designated emergency evacuation route signs on main roads.",
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
            affectedArea = "Kathu District & Hillside Slopes",
            distanceKm = 8.5,
            timestamp = "1 hour ago • 13:40 Local Time",
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
            disasterType = "High Winds & Gale",
            severity = AlertSeverity.ADVISORY,
            affectedArea = "Southern Coastal Islands",
            distanceKm = 14.0,
            timestamp = "3 hours ago • 11:15 Local Time",
            sourceLabel = "[SIMULATED DISASTER ALERT] Regional Weather Center",
            description = "Sustained wind speeds up to 55 km/h with gusts up to 75 km/h expected through late evening. Ferry services temporarily suspended.",
            officialInstructions = listOf(
                "Secure loose outdoor objects and balcony items.",
                "Avoid standing near tall trees or unsecured scaffolding."
            ),
            recommendedActions = listOf(
                "Confirm local transit schedules before departing."
            )
        )
    )

    val accommodations = listOf(
        SafeAccommodation(
            id = "acc-101",
            name = "Old Town Safe Haven Resort",
            type = "Certified Disaster Evacuation Partner",
            address = "42 Dibuk Road, Old Phuket Town",
            distanceKm = 1.2,
            pricePerNight = "$45 / night",
            rating = 4.9,
            reviewCount = 128,
            capacityAvailable = 18,
            maxCapacity = 120,
            isEmergencyShelter = true,
            emergencyAmenities = listOf("Backup Diesel Generator", "24/7 First Aid Staff", "Clean Water Purification", "Concrete Earthquake Structure"),
            contactPhone = "+66 76 340 112"
        ),
        SafeAccommodation(
            id = "acc-102",
            name = "Highland Hilltop Eco Hotel",
            type = "High-Elevation Safe Hotel",
            address = "188 Phuket Hill Ridge, Kathu",
            distanceKm = 4.8,
            pricePerNight = "$65 / night",
            rating = 4.8,
            reviewCount = 94,
            capacityAvailable = 8,
            maxCapacity = 80,
            isEmergencyShelter = false,
            emergencyAmenities = listOf("High Altitude Elevation (85m above sea level)", "Solar Backup Power", "Satellite Internet", "Emergency Rations"),
            contactPhone = "+66 76 892 400"
        ),
        SafeAccommodation(
            id = "acc-103",
            name = "Phuket Central Evacuation Center",
            type = "Government Designated Free Shelter",
            address = "Municipal Sports Complex, Amphoe Mueang",
            distanceKm = 2.5,
            pricePerNight = "FREE Emergency Shelter",
            rating = 4.6,
            reviewCount = 210,
            capacityAvailable = 150,
            maxCapacity = 500,
            isEmergencyShelter = true,
            emergencyAmenities = listOf("Red Cross Medical Triage", "Free Emergency Meals", "Multilingual Interpreters", "Child Safety Zone"),
            contactPhone = "1784 (Disaster Hotline)"
        )
    )

    val propertyReviews = listOf(
        PropertyReview(
            id = "rev-1",
            accommodationId = "acc-101",
            userName = "Sarah Jenkins",
            userCountry = "Australia",
            rating = 5.0,
            date = "Yesterday",
            comment = "During the sudden coastal advisory, the staff at Old Town Safe Haven was incredible! They instantly alerted all guests and had clear evacuation maps in 4 languages."
        ),
        PropertyReview(
            id = "rev-2",
            accommodationId = "acc-101",
            userName = "Marco Rossi",
            userCountry = "Italy",
            rating = 4.8,
            date = "3 days ago",
            comment = "Very sturdy building, high elevation from the beach, and great backup power when the heavy storms cut the city grid."
        ),
        PropertyReview(
            id = "rev-3",
            accommodationId = "acc-102",
            userName = "Kenji Sato",
            userCountry = "Japan",
            rating = 5.0,
            date = "1 week ago",
            comment = "An ideal safe spot if you are looking for high elevation away from tsunami surge zones. Extremely clean and professional staff."
        )
    )

    val hazardZones = listOf(
        HazardZone("hz-1", "Patong Beach Surge Zone", "Tsunami Coastal Hazard Zone", 0.35f, 0.45f, 0.18f),
        HazardZone("hz-2", "Kathu Slope Mudslide Risk", "Hillside Flash Flood Area", 0.65f, 0.28f, 0.14f)
    )

    val mapMarkers = listOf(
        MapMarker("m-1", "Current Location (Phuket Town)", MarkerType.USER_LOCATION, 0.0, "Your real-time GPS location (Simulated)", 0.52f, 0.58f),
        MapMarker("m-2", "Old Town Safe Haven Resort", MarkerType.SAFE_ACCOMMODATION, 1.2, "Elevation 25m - Safe Certified", 0.58f, 0.52f),
        MapMarker("m-3", "Highland Hilltop Eco Hotel", MarkerType.SAFE_ACCOMMODATION, 4.8, "Elevation 85m - High Ground", 0.72f, 0.35f),
        MapMarker("m-4", "Phuket Central Free Shelter", MarkerType.EMERGENCY_SHELTER, 2.5, "Designated Primary Civic Shelter", 0.45f, 0.65f),
        MapMarker("m-5", "Coastal Hazard Zone", MarkerType.HAZARD_ZONE, 3.2, "Evacuation Zone A", 0.32f, 0.48f)
    )

    val initialEmergencyContacts = listOf(
        EmergencyContact("c-1", "Emma Watson", "Spouse / Family", "+1 (555) 019-2834", isPrimary = true),
        EmergencyContact("c-2", "David Miller", "Travel Partner", "+66 81 234 5678", isPrimary = false),
        EmergencyContact("c-3", "Embassy Emergency Line", "Official Embassy", "+66 2 205 4000", isPrimary = false)
    )

    val initialNotifications = listOf(
        "SIMULATED: Coastal Tsunami Advisory issued for West Phuket - 10 mins ago",
        "SIMULATED: Emergency Shelter opened at Municipal Sports Complex - 45 mins ago",
        "System: TourSafe downloaded offline safety guides for Thailand - 2 hours ago",
        "System: Language preference set to English - Yesterday"
    )
}
