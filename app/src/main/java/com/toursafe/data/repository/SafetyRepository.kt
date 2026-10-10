package com.toursafe.data.repository

import com.toursafe.data.mock.MockData
import com.toursafe.data.models.DisasterAlert
import com.toursafe.data.models.SafeAccommodation
import com.toursafe.data.models.UserSafetyStatus
import com.toursafe.util.LocationUtils

interface SafetyRepository {
    fun getUserStatus(): UserSafetyStatus
    fun getAlerts(userLat: Double = 0.0, userLng: Double = 0.0): List<DisasterAlert>
    fun getAccommodations(userLat: Double = 0.0, userLng: Double = 0.0): List<SafeAccommodation>
}

class MockSafetyRepository : SafetyRepository {
    override fun getUserStatus(): UserSafetyStatus = UserSafetyStatus()

    override fun getAlerts(userLat: Double, userLng: Double): List<DisasterAlert> {
        if (userLat == 0.0 && userLng == 0.0) return MockData.alerts
        return MockData.alerts.map { alert ->
            val dist = LocationUtils.calculateDistanceKm(userLat, userLng, alert.latitude, alert.longitude)
            alert.copy(distanceKm = dist)
        }.sortedBy { it.distanceKm }
    }

    override fun getAccommodations(userLat: Double, userLng: Double): List<SafeAccommodation> {
        if (userLat == 0.0 && userLng == 0.0) return MockData.accommodations
        return MockData.accommodations.map { acc ->
            val dist = LocationUtils.calculateDistanceKm(userLat, userLng, acc.latitude, acc.longitude)
            acc.copy(distanceKm = dist)
        }.sortedBy { it.distanceKm }
    }
}
