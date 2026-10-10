package com.toursafe.data.models

data class SafeAccommodation(
    val id: String,
    val name: String,
    val type: String, // e.g., "Certified Shelter", "Resilient Hotel", "Community Safehouse"
    val address: String,
    val distanceKm: Double,
    val pricePerNight: String? = null,
    val rating: Double? = null,
    val reviewCount: Int = 0,
    val capacityAvailable: Int? = null,
    val maxCapacity: Int? = null,
    val isEmergencyShelter: Boolean = true,
    val emergencyAmenities: List<String> = emptyList(),
    val contactPhone: String? = null,
    val latitude: Double = 7.8842,
    val longitude: Double = 98.3891,
    val imageUrl: String = "",
    val isDemo: Boolean = true,
    val isVerified: Boolean = false
)
