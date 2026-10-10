package com.toursafe.data.models

data class SafeAccommodation(
    val id: String,
    val name: String,
    val type: String, // e.g., "Certified Shelter", "Resilient Hotel", "Community Safehouse"
    val address: String,
    val distanceKm: Double,
    val pricePerNight: String,
    val rating: Double,
    val reviewCount: Int,
    val capacityAvailable: Int,
    val maxCapacity: Int,
    val isEmergencyShelter: Boolean,
    val emergencyAmenities: List<String>, // e.g. "Backup Generator", "First Aid", "Clean Water Storage"
    val contactPhone: String,
    val imageUrl: String = ""
)
